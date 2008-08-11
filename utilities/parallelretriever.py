#!/usr/bin/env python
""" 
This "parallel retriever" takes as input a "file manifest" and
"retrieval order" for a remotely available BagIt package, and launches
a specified number of parallel instances of an appropriate retriever
tool (rsync, wget) in order to retrieve the contents of the package.
It produces a BagIt package as output. If not supplied, the name of
the package will default to the current timestamp (seconds since Dec
31 1969).
    
rsync and wget must be available in the $PATH. 

If an rsync password is necessary, it must be supplied via the environment,
as in:
  RSYNC_PASSWORD=pass ./parallelretriever.py -m manifest-md5.txt -r fetch.txt 

Run with '-h' for documentation.
"""

import os
import sys

if sys.version_info < (2,5,0):
    print """\
WARNING: this script is being developed and tested against Python 2.5.x.
  And you're running with Python version %s
""" % sys.version

import time
import Queue
import shutil
import urllib2
import logging
import threading
import subprocess

from optparse import OptionParser


class ProgressReporter(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self._queue = Queue.Queue()
        self._in_progress = set()

    def started(self, filename):
        self._in_progress.add(filename)        

    def finished(self, filename):
        self._queue.put(filename)

    def run(self):
        total = 0
        t0 = time.time()
        while True:
            while True:
                try:
                    filename = self._queue.get(block=False)
                    self._queue.task_done()
                    self._in_progress.remove(filename)
                    mode, ino, dev, nlink, uid, gid, size, atime, mtime, ctime = os.stat(filename)
                    total += size
                except Queue.Empty, e:
                    break
            time.sleep(1)
            in_progress_total = 0
            for fn in set(self._in_progress):
                if os.path.exists(fn):
                    mode, ino, dev, nlink, uid, gid, size, atime, mtime, ctime = os.stat(fn)
                    in_progress_total += size
            g_total = total + in_progress_total
            duration = (time.time() - t0)
            m_bytes_per_second = g_total / duration / 1024. / 1024.
            logging.info("fetched %u bytes in %.4g seconds. %.4g MB/s (overall)" % 
                         (g_total, duration, m_bytes_per_second))
            print "\rfetched %u bytes in %.4g in seconds. %.4g MB/s (overall)" % (g_total, duration, m_bytes_per_second),
            sys.stdout.flush()

progress_reporter = ProgressReporter()
progress_reporter.setName("Progress Reporter")
progress_reporter.setDaemon(True)
progress_reporter.start()


class FetchWorker(threading.Thread):
    """
    Worker to fetch items in fetch.txt
    """
    def __init__(self, queue, package_directory):
        threading.Thread.__init__(self)
        self.queue = queue
        self.package_directory = package_directory

    def run(self):
        while True:
            item = self.queue.get()
            try:
                url, filesize, filename = item
                filename = os.path.join(self.package_directory, filename)
                fetch(filename, url)
            finally:
                self.queue.task_done()
            logging.debug("size: %s" % self.queue.qsize())


def generate_package_identifier():
    """Assume no more than one of these packages per second, and 
       use MIME-encoded seconds-since-epoch."""
    return str(int(time.time()))


def _subprocess(cmd):
    p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = p.communicate()
    if p.returncode:
        logging.info("""
Subprocess Info
---------------
cmd: %s 

stdout: 
'''
%s 
'''

stderr: 
'''
%s
'''
---------------
""" % (" ".join(cmd), stdout, stderr))
        raise subprocess.CalledProcessError(p.returncode, cmd)

def fetch(filename, url):
    logging.debug("%s fetching: %s" % (threading.currentThread().getName(), url))
    progress_reporter.started(filename)
    try:
        os.makedirs(os.path.dirname(filename))
    except OSError:
        pass # it's OK if the directories are already there
    if url.startswith('http') or url.startswith('https'):
        cmd = ["wget", "-O", filename, url]
        _subprocess(cmd)
    elif url.startswith('rsync'):
        cmd = ["rsync", "-ar", url, filename]
        _subprocess(cmd)
    elif url.startswith('file'):
        url = urllib2.urlopen(url)
        f = file(filename, "wb")
        while True:
            d = url.read(1024)
            if d:
                f.write(d)
            else:
                f.close()
                break
    else:
        raise Exception("unexpected url type")
    finished_queue.put(filename)


def retrieve_package(options):
    """
    Retrieve the package using the desired number of workers.
    """
    logging.info("Retrieving Package with options: %s" % repr(options))
    package_directory = os.path.join(options.destination_path,
                                     options.package_identifier)
    if not os.path.isdir(package_directory):
        os.mkdir(package_directory)

    shutil.copy(options.file_manifest,   package_directory)
    shutil.copy(options.retrieval_order, package_directory)
                    
    queue = Queue.Queue()

    # spawn a pool of worker threads
    for i in range(options.num_processes):
        t = FetchWorker(queue, package_directory)
        t.setName("Fetch Worker #%d" % i)
        t.setDaemon(True)
        t.start()

    # populate queue with fetch items
    for line in file(options.retrieval_order).readlines():
        parts = line.strip().split()
        if len(parts)==0:
            logging.warning("skipping over blank line")
            continue
        if len(parts)!=3:
            logging.error("line does not have three parts as expected. Line: '%s'" % line)
            sys.exit(-1)
        item = tuple(parts)
        queue.put(item)

    #wait on the queue until everything has been processed     
    queue.join()


if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option("-n", "--number-of-processes", dest="num_processes", 
                      type="int", default=16, 
                      help="number of concurrent retrievers to run")
    parser.add_option("-i", "--package-identifier", dest="package_identifier",
                      help="unique identifier for package (will be auto-generated if not supplied)")
    parser.add_option("-m", "--file-manifest", dest="file_manifest",
                      help="path to the file manifest that defines this package")
    parser.add_option("-r", "--retrieval-order", dest="retrieval_order",
                      help="path to the retrieval order (fetch.txt) for this package")
    parser.add_option("-d", "--destination-path", dest="destination_path",
                      help="path in which to create the package")
    options, args = parser.parse_args()

    if options.file_manifest is None:
        parser.error("Supply a file manifest with -m.")

    if options.retrieval_order is None:
        parser.error("Supply a retrieval order with -r.")

    if options.package_identifier is None:
        options.package_identifier = generate_package_identifier()

    if options.destination_path is None:
        options.destination_path = os.getcwd()

    logging.basicConfig(level=logging.INFO, 
                        format='%(asctime)s %(levelname)-8s %(message)s',
                        datefmt='%Y-%m-%d %H:%M:%S',
                        filename='%s-retrieval.log' % options.package_identifier)

    try:
        retrieve_package(options)
    except KeyboardInterrupt, ki:
        logging.info("Bye Bye")
    except Exception, e:
        logging.exception(e)
