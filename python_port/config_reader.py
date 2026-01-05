import yaml
import os

class ConfigReader:
    """
    配置读取器，用于从共享配置文件中读取配置
    """
    
    def __init__(self, config_path=None):
        """
        初始化配置读取器
        
        Args:
            config_path (str): 配置文件路径，默认使用项目根目录下的config/config.yml
        """
        if config_path is None:
            # 默认使用项目根目录下的config/config.yml
            self.config_path = os.path.join(
                os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                'config',
                'config.yml'
            )
        else:
            self.config_path = config_path
        
        self.config = self._load_config()
    
    def _load_config(self):
        """
        加载配置文件
        
        Returns:
            dict: 配置字典
        """
        try:
            with open(self.config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
            return config
        except FileNotFoundError:
            print(f"配置文件未找到: {self.config_path}")
            return {}
        except yaml.YAMLError as e:
            print(f"配置文件解析错误: {e}")
            return {}
    
    def get(self, key, default=None):
        """
        获取配置值，支持点号分隔的嵌套路径
        
        Args:
            key (str): 配置键，支持点号分隔的嵌套路径，如 'python.port'
            default: 默认值，当配置不存在时返回
        
        Returns:
            配置值或默认值
        """
        keys = key.split('.')
        value = self.config
        
        try:
            for k in keys:
                value = value[k]
            return value
        except (KeyError, TypeError):
            return default
    
    def reload(self):
        """
        重新加载配置文件
        """
        self.config = self._load_config()

# 创建全局配置实例
config = ConfigReader()
