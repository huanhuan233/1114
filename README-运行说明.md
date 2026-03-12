# 工艺智能体 - 运行说明

## 1. 手动启动（推荐）

先启动后端，再启动前端；每个在单独终端里运行，便于看日志。

**终端一 - 后端（端口 8055）：**
```bash
cd /data/PXY/cappall/prototype/backend
./mvnw spring-boot:run
```

**终端二 - 前端（端口 5355）：**
```bash
cd /data/PXY/cappall/prototype/frontend
npm run dev -- --host 0.0.0.0
```

然后在浏览器打开：本机用 `http://127.0.0.1:5355/`，局域网用 `http://<本机IP>:5355/`。

---

## 2. 脚本一键启动

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

## 3. 数据库服务未找到（Unit mysql.service not found）

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

## 4. 无法访问此网站 / ERR_CONNECTION_REFUSED

- **必须带端口访问**：前端地址是 `http://<IP>:5355/`，不要漏掉 `:5355`。例如：
  - 本机：`http://127.0.0.1:5355/`
  - 局域网：`http://192.168.0.97:5355/`
- **确认服务已启动**：在服务器上执行：
  ```bash
  cd /data/PXY/cappall/prototype
  tail -f logs/frontend.log logs/backend.log
  ```
  若有报错，先解决再访问。确认端口在监听：
  ```bash
  ss -tlnp | grep -E '5355|8055'
  ```
- **从其他电脑访问**：若在 Windows 等机器用浏览器访问 192.168.0.97，需在**服务器**上放行端口：
  ```bash
  sudo ufw allow 5355
  sudo ufw allow 8055
  sudo ufw reload
  ```
  若未启用 ufw，可检查 iptables 或云安全组是否放行 5355、8055。

