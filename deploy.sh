#!/bin/bash
# PetFit 배포 스크립트
# EC2에서 실행

set -e
echo "========================================="
echo "  PetFit Deploy"
echo "========================================="

# 프로젝트 디렉토리
PROJECT_DIR=~/petfit
BACKEND_DIR=$PROJECT_DIR/PetFit-BE
FRONTEND_DIR=$PROJECT_DIR/PetFit-Front

# EC2 퍼블릭 IP 자동 감지
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "localhost")
echo "EC2 IP: $EC2_IP"

# 1. 프로젝트 클론/업데이트
echo ""
echo "[1/4] 소스코드 다운로드..."
mkdir -p $PROJECT_DIR

if [ -d "$BACKEND_DIR" ]; then
    cd $BACKEND_DIR && git pull origin develop
else
    git clone -b develop https://github.com/P-Project8/PetFit-BE.git $BACKEND_DIR
fi

if [ -d "$FRONTEND_DIR" ]; then
    cd $FRONTEND_DIR && git pull origin main
else
    git clone https://github.com/P-Project8/PetFit-Front.git $FRONTEND_DIR
fi

# 2. application-secret.yml 생성
echo ""
echo "[2/4] 설정 파일 생성..."

cat > $BACKEND_DIR/src/main/resources/application-secret.yml << SECRETEOF
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/\${DB_NAME:petfit}
    username: \${DB_USER:postgres}
    password: \${DB_PASSWORD:petfit2026}
    driver-class-name: org.postgresql.Driver
  data:
    redis:
      host: redis
      port: 6379
  mail:
    host: smtp.gmail.com
    port: 587
    username: \${MAIL_USERNAME:}
    password: \${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

jwt:
  key: \${JWT_KEY:petfitprojectsecretkey1234567890abcdefghijklmnopqrstuvwxyz}
  access:
    expiration: 3600000
  refresh:
    expiration: 604800000

email:
  from: \${EMAIL_FROM:}

GEMINI_API_KEY: \${GEMINI_API_KEY:}
SECRETEOF

# 3. 백엔드 JAR 빌드
echo ""
echo "[3/4] 백엔드 빌드..."
cd $BACKEND_DIR
chmod +x gradlew
./gradlew bootJar -x test --no-daemon

# 4. Docker Compose 실행
echo ""
echo "[4/4] Docker 컨테이너 시작..."
cd $BACKEND_DIR

# .env 파일 생성 (환경변수)
cat > .env << ENVEOF
EC2_IP=$EC2_IP
DB_USER=postgres
DB_PASSWORD=petfit2026
DB_NAME=petfit
JWT_KEY=petfitprojectsecretkey1234567890abcdefghijklmnopqrstuvwxyz
EMAIL_FROM=${EMAIL_FROM:-}
MAIL_USERNAME=${MAIL_USERNAME:-}
MAIL_PASSWORD=${MAIL_PASSWORD:-}
GEMINI_API_KEY=${GEMINI_API_KEY:-}
ENVEOF

# 기존 컨테이너 정리 후 재시작
docker compose -f docker-compose.prod.yml down 2>/dev/null || true
docker compose -f docker-compose.prod.yml up -d --build

echo ""
echo "========================================="
echo "  배포 완료!"
echo ""
echo "  Swagger:  http://$EC2_IP:8080/swagger-ui/index.html"
echo "  Frontend: http://$EC2_IP"
echo "========================================="
