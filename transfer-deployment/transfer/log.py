import logging

class Log():
    def __init__(self, project_name="Transfer"):
        self.logger = logging.getLogger(project_name)
        formatter = logging.Formatter('%(asctime)s %(name)s %(levelname)s %(message)s')
        logfile = logging.FileHandler('logs/transfer.log')
        logfile.setFormatter(formatter)
        console = logging.StreamHandler()
        console.setFormatter(formatter)
        self.logger.addHandler(logfile)
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
