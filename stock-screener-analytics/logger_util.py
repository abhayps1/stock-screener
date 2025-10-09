
import logging
import threading
from colorama import Fore, Style, init
from datetime import datetime

init(autoreset=True)

class ColoredFormatter(logging.Formatter):
    def format(self, record):
        # Time in HH:mm:ss.SSS
        ct = datetime.fromtimestamp(record.created)
        time_str = ct.strftime('%H:%M:%S.%f')[:-3]
        time_colored = f"{Fore.GREEN}{time_str}{Style.RESET_ALL}"
       
        # Level
        level_colored = f"{Fore.BLUE}{record.levelname:<5}{Style.RESET_ALL}"
       
        # Thread
        thread_colored = f"{Fore.RED}[{record.threadName}]{Style.RESET_ALL}"
       
        # Logger name (max 15 chars)
        logger_colored = f"{Fore.RED}{record.name[:15]:<15}{Style.RESET_ALL}"
       
        # Message
        msg = record.getMessage()
       
        # log_line = f"{time_colored} {level_colored} {thread_colored} {logger_colored} - {msg}"
        log_line = f"{time_colored} {level_colored} {logger_colored} - {msg}"
        return log_line


def get_console_logger(name: str = "AppLogger", level=logging.INFO):
    logger = logging.getLogger(name)
    logger.setLevel(level)
    if not logger.handlers:
        ch = logging.StreamHandler()
        ch.setLevel(level)
        ch.setFormatter(ColoredFormatter())
        logger.addHandler(ch)
    return logger

