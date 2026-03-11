# 工艺智能体 - 运行说明

## 1. 脚本位置

- **一键脚本（推荐）**：在项目根目录执行  
  ```bash
  cd /data/PXY/cappall
  ./setup-and-start.sh
  ```
  或在 prototype 目录下执行：
  ```bash
  cd /data/PXY/cappall/prototype
  ./setup-and-start.sh
  ```

- 若提示 `No such file or directory`，请确认当前目录并加上执行权限：
  ```bash
  chmod +x /data/PXY/cappall/setup-and-start.sh
  /data/PXY/cappall/setup-and-start.sh
  ```

## 2. 数据库服务未找到（Unit mysql.service not found）

本机很可能使用的是 **MariaDB** 而不是 MySQL，且可能尚未安装数据库**服务端**。

**安装 MariaDB 服务端：**
```bash
sudo apt update
sudo apt install -y mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

（可选）设置 root 密码并与 `prototype/backend/src/main/resources/application.yml` 中的 `spring.datasource.password` 保持一致：
```bash
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'huanhuan123';"
```

**然后执行一键脚本：**
```bash
cd /data/PXY/cappall
./setup-and-start.sh
```

