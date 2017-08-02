########## 公共环境配置 ##########
# 字符集
CHARSET_NAME = UTF-8
########## 日志信息配置 ##########
# 日志级别  不区分大小写  debug:调试信息  info:普通信息   WARN:警告信息  ERROR:错误信息  FATAL:严重错误信息
LOG_LEVEL = ${USE_LEVEL}
# 是否输出到控制台(默认为false)
CONSOLE_PRINT = ${USE_CONSOLE}
# 是否输出到文件(默认为true)
CONSOLE_FILE = ${USE_FILELOG}
# 日志文件存放路径
LOG_PATH =../flog/gamesr
# 日志写入文件的间隔时间(默认为10毫秒)
WRITE_LOG_INV_TIME = 10
# 单个日志文件的大小(默认为100M)
SINGLE_LOG_FILE_SIZE = 104857600
# 单个日志文件缓存的大小(默认为10KB)
SINGLE_LOG_CACHE_SIZE = 512
