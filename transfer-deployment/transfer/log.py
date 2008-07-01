import os
import logging

class Log():
    def __init__(self, project_name="Transfer"):
        self.logger = logging.getLogger(project_name)
        logdir = 'logs'
        logfile = '%s/transfer.log' % (logdir)
        formatter = logging.Formatter('%(asctime)s %(name)s %(levelname)s %(message)s')
        try:
            open(logfile, 'r+')
        except IOError, e:
            if e.errno == 13:
                raise RuntimeError("Need write permission on logfile: %s" % (logfile))
            elif e.errno == 2:
                # file does not exist; create it and move on
                try:
                    os.makedirs(logdir)
                except IOError:
                    # logdir already exists, so eat the tasty error
                    pass
                open(logfile, 'w').close()
            else:
                raise RuntimeError("Unhandled error opening logfile %s: %s" % (logfile, e))
        handler = logging.FileHandler(logfile)
        handler.setFormatter(formatter)
        console = logging.StreamHandler()
        console.setFormatter(formatter)
        self.logger.addHandler(handler)
        self.logger.addHandler(console)
        self.logger.setLevel(logging.DEBUG)

    def debug(self, message):
        self.logger.debug(message)

    def info(self, message):
        self.logger.info(message)

    def warning(self, message):
        self.logger.warning(message)

    def error(self, message):
        self.logger.error(message)
