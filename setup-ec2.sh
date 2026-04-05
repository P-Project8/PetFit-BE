#!/bin/bash
# PetFit EC2 초기 설정 스크립트
# Ubuntu 22.04에서 실행

set -e
echo "========================================="
echo "  PetFit EC2 Setup"
echo "========================================="

# 1. 시스템 업데이트
echo "[1/5] 시스템 업데이트..."
sudo apt-get update -y
sudo apt-get upgrade -y

# 2. Docker 설치
echo "[2/5] Docker 설치..."
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Docker 권한 설정
sudo usermod -aG docker $USER
echo "Docker 설치 완료!"

# 3. Git 설치
echo "[3/5] Git 설치..."
sudo apt-get install -y git

# 4. Java 17 설치 (백엔드 빌드용)
echo "[4/5] Java 17 설치..."
sudo apt-get install -y openjdk-17-jdk-headless

# 5. Swap 메모리 설정 (2GB)
echo "[5/5] Swap 설정 (2GB)..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    echo "Swap 2GB 설정 완료"
else
    echo "Swap 이미 존재"
fi

echo ""
echo "========================================="
echo "  설정 완료!"
echo "  Docker 그룹 적용을 위해 재로그인하세요:"
echo "  exit 후 다시 SSH 접속"
echo "========================================="
