-- users 테이블에 role 컬럼 추가 (이미 존재하면 무시)
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

