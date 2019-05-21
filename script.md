## Docker Container 
1. 도커로 PostgreSQL 실행
docker run --name db_name -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
2. 도커 컨테이너 내부로 진입
docker exec -i -t db_name base 
su - postgres
3. PostgreSQL Client 툴을 이용해 DB 진입
psql -d postgres -U postgres
4. db 조회
\l , \dt

