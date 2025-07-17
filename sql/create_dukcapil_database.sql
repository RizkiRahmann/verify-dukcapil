-- ===== DUKCAPIL DATABASE SETUP SCRIPT =====
-- File: create_dukcapil_database.sql
-- Run: psql -U postgres -f create_dukcapil_database.sql

-- 1. Create Database
CREATE DATABASE dukcapil_ktp;

-- 2. Connect to dukcapil_ktp database
\c dukcapil_ktp;

-- 3. Create KTP Dukcapil Table
CREATE TABLE IF NOT EXISTS ktp_dukcapil (
    id BIGSERIAL PRIMARY KEY,
    nik VARCHAR(20) NOT NULL UNIQUE,
    nama_lengkap VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(50) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(20) NOT NULL CHECK (jenis_kelamin IN ('LAKI_LAKI', 'PEREMPUAN')),
    nama_alamat TEXT NOT NULL,
    kecamatan VARCHAR(50) NOT NULL,
    kelurahan VARCHAR(50) NOT NULL,
    agama VARCHAR(20) NOT NULL CHECK (agama IN ('ISLAM', 'KRISTEN', 'BUDDHA', 'HINDU', 'KONGHUCU', 'LAINNYA')),
    status_perkawinan VARCHAR(20) NOT NULL,
    kewarganegaraan VARCHAR(20) DEFAULT 'WNI' NOT NULL,
    berlaku_hingga VARCHAR(20) DEFAULT 'SEUMUR HIDUP' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create Indexes untuk Performance
CREATE INDEX IF NOT EXISTS idx_ktp_nik ON ktp_dukcapil(nik);
CREATE INDEX IF NOT EXISTS idx_ktp_nama ON ktp_dukcapil(LOWER(nama_lengkap));
CREATE INDEX IF NOT EXISTS idx_ktp_nik_nama ON ktp_dukcapil(nik, LOWER(nama_lengkap));
CREATE INDEX IF NOT EXISTS idx_ktp_tanggal_lahir ON ktp_dukcapil(tanggal_lahir);
CREATE INDEX IF NOT EXISTS idx_ktp_jenis_kelamin ON ktp_dukcapil(jenis_kelamin);
CREATE INDEX IF NOT EXISTS idx_ktp_agama ON ktp_dukcapil(agama);
CREATE INDEX IF NOT EXISTS idx_ktp_tempat_lahir ON ktp_dukcapil(tempat_lahir);

-- 5. Create Trigger untuk updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_ktp_updated_at
    BEFORE UPDATE ON ktp_dukcapil
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 6. Insert Sample Data KTP (Data dummy yang realistis)
INSERT INTO ktp_dukcapil (
    nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_alamat, kecamatan, kelurahan, agama, status_perkawinan
) VALUES 
-- Jakarta Area
('3175031234567890', 'John Doe', 'Jakarta', '1990-05-15', 'LAKI_LAKI',
 'Jl. Sudirman No. 123, RT 001/RW 002', 'Tanah Abang', 'Bendungan Hilir', 'ISLAM', 'BELUM KAWIN'),

('3175032345678901', 'Jane Smith', 'Jakarta', '1995-08-22', 'PEREMPUAN', 
 'Jl. Gatot Subroto No. 456, RT 003/RW 004', 'Setiabudi', 'Kuningan Timur', 'KRISTEN', 'KAWIN'),

('3175033456789012', 'Ahmad Rahman', 'Bogor', '1985-12-10', 'LAKI_LAKI',
 'Jl. Thamrin No. 789, RT 005/RW 006', 'Menteng', 'Gondangdia', 'ISLAM', 'KAWIN'),

('3175034567890123', 'Siti Nurhaliza', 'Depok', '1992-03-18', 'PEREMPUAN',
 'Jl. HR Rasuna Said No. 321, RT 007/RW 008', 'Setiabudi', 'Setiabudi', 'ISLAM', 'BELUM KAWIN'),

('3175035678901234', 'Budi Santoso', 'Jakarta', '1988-11-25', 'LAKI_LAKI',
 'Jl. Kemang Raya No. 654, RT 009/RW 010', 'Mampang Prapatan', 'Kemang', 'BUDDHA', 'KAWIN'),

-- Jakarta Barat
('3174011234567890', 'Maria Gonzales', 'Jakarta', '1993-07-14', 'PEREMPUAN',
 'Jl. Daan Mogot No. 111, RT 001/RW 001', 'Cengkareng', 'Cengkareng Barat', 'KRISTEN', 'BELUM KAWIN'),

('3174012345678901', 'Rudi Hermawan', 'Bekasi', '1987-09-30', 'LAKI_LAKI',
 'Jl. Puri Kembangan No. 222, RT 002/RW 003', 'Kebon Jeruk', 'Sukabumi Utara', 'ISLAM', 'KAWIN'),

-- Jakarta Timur  
('3175071234567890', 'Dewi Sartika', 'Jakarta', '1991-11-08', 'PEREMPUAN',
 'Jl. Raya Bekasi No. 333, RT 004/RW 005', 'Cakung', 'Cakung Barat', 'ISLAM', 'KAWIN'),

('3175072345678901', 'Andi Wijaya', 'Tangerang', '1989-04-12', 'LAKI_LAKI',
 'Jl. Kalimalang No. 444, RT 006/RW 007', 'Duren Sawit', 'Pondok Bambu', 'HINDU', 'BELUM KAWIN'),

-- Jakarta Utara
('3174051234567890', 'Linda Kusuma', 'Medan', '1994-06-20', 'PEREMPUAN',
 'Jl. Ancol Barat No. 555, RT 008/RW 009', 'Pademangan', 'Pademangan Barat', 'BUDDHA', 'BELUM KAWIN'),

-- Bandung
('3273011234567890', 'Dedi Setiawan', 'Bandung', '1986-01-25', 'LAKI_LAKI',
 'Jl. Asia Afrika No. 666, RT 010/RW 011', 'Sumur Bandung', 'Braga', 'ISLAM', 'KAWIN'),

('3273012345678901', 'Rina Melati', 'Garut', '1990-12-05', 'PEREMPUAN',
 'Jl. Dago No. 777, RT 012/RW 013', 'Coblong', 'Dago', 'KRISTEN', 'BELUM KAWIN'),

-- Surabaya
('3578011234567890', 'Agus Salim', 'Surabaya', '1988-08-15', 'LAKI_LAKI',
 'Jl. Pemuda No. 888, RT 014/RW 015', 'Gubeng', 'Gubeng', 'ISLAM', 'KAWIN'),

('3578012345678901', 'Ratna Dewi', 'Malang', '1992-10-28', 'PEREMPUAN',
 'Jl. Darmo No. 999, RT 016/RW 017', 'Wonokromo', 'Darmo', 'HINDU', 'BELUM KAWIN'),

-- Yogyakarta  
('3471011234567890', 'Bambang Nugroho', 'Yogyakarta', '1985-03-22', 'LAKI_LAKI',
 'Jl. Malioboro No. 101, RT 018/RW 019', 'Jetis', 'Cokrodiningratan', 'ISLAM', 'KAWIN'),

-- Medan
('1271011234567890', 'Sari Dewi', 'Medan', '1991-09-17', 'PEREMPUAN',
 'Jl. Imam Bonjol No. 202, RT 020/RW 021', 'Medan Petisah', 'Petisah Tengah', 'ISLAM', 'KAWIN'),

-- Makassar
('7371011234567890', 'Fikri Ramadhan', 'Makassar', '1993-03-12', 'LAKI_LAKI',
 'Jl. Sultan Alauddin No. 303, RT 022/RW 023', 'Rappocini', 'Mappala', 'ISLAM', 'BELUM KAWIN'),

-- Balikpapan
('6471011234567890', 'Nova Andriani', 'Balikpapan', '1989-11-30', 'PEREMPUAN',
 'Jl. Ahmad Yani No. 404, RT 024/RW 025', 'Balikpapan Kota', 'Klandasan Ilir', 'KRISTEN', 'KAWIN'),

-- Denpasar
('5171011234567890', 'I Made Adi', 'Denpasar', '1987-06-08', 'LAKI_LAKI',
 'Jl. Gajah Mada No. 505, RT 026/RW 027', 'Denpasar Barat', 'Pemecutan Kaja', 'HINDU', 'KAWIN'),

-- Test data untuk verifikasi specific
('1234567890123456', 'Test User One', 'Jakarta', '1995-01-01', 'LAKI_LAKI',
 'Jl. Test No. 123', 'Test Kecamatan', 'Test Kelurahan', 'ISLAM', 'BELUM KAWIN'),

('1234567890123457', 'Test User Two', 'Bandung', '1992-02-02', 'PEREMPUAN',
 'Jl. Test No. 456', 'Test Kecamatan', 'Test Kelurahan', 'KRISTEN', 'KAWIN'),

('9876543210987654', 'Admin Test', 'Jakarta', '1980-12-31', 'LAKI_LAKI',
 'Jl. Admin No. 999', 'Admin Kecamatan', 'Admin Kelurahan', 'ISLAM', 'KAWIN');

-- 7. Create Views untuk Reports (Optional)
CREATE VIEW ktp_summary AS
SELECT 
    COUNT(*) as total_records,
    COUNT(CASE WHEN jenis_kelamin = 'LAKI_LAKI' THEN 1 END) as total_laki_laki,
    COUNT(CASE WHEN jenis_kelamin = 'PEREMPUAN' THEN 1 END) as total_perempuan,
    COUNT(CASE WHEN agama = 'ISLAM' THEN 1 END) as total_islam,
    COUNT(CASE WHEN agama = 'KRISTEN' THEN 1 END) as total_kristen,
    COUNT(CASE WHEN agama = 'BUDDHA' THEN 1 END) as total_buddha,
    COUNT(CASE WHEN agama = 'HINDU' THEN 1 END) as total_hindu,
    COUNT(CASE WHEN status_perkawinan = 'KAWIN' THEN 1 END) as total_kawin,
    COUNT(CASE WHEN status_perkawinan = 'BELUM KAWIN' THEN 1 END) as total_belum_kawin
FROM ktp_dukcapil;

-- 8. Create Function untuk Search (Advanced)
CREATE OR REPLACE FUNCTION search_ktp(
    search_nik VARCHAR DEFAULT NULL,
    search_nama VARCHAR DEFAULT NULL,
    search_tempat_lahir VARCHAR DEFAULT NULL
)
RETURNS TABLE(
    nik VARCHAR,
    nama_lengkap VARCHAR,
    tempat_lahir VARCHAR,
    tanggal_lahir DATE,
    jenis_kelamin VARCHAR,
    agama VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        k.nik,
        k.nama_lengkap,
        k.tempat_lahir,
        k.tanggal_lahir,
        k.jenis_kelamin,
        k.agama
    FROM ktp_dukcapil k
    WHERE 
        (search_nik IS NULL OR k.nik = search_nik)
        AND (search_nama IS NULL OR LOWER(k.nama_lengkap) LIKE LOWER('%' || search_nama || '%'))
        AND (search_tempat_lahir IS NULL OR LOWER(k.tempat_lahir) LIKE LOWER('%' || search_tempat_lahir || '%'));
END;
$$ LANGUAGE plpgsql;

-- 9. Grant Permissions (Optional)
-- GRANT ALL PRIVILEGES ON DATABASE dukcapil_ktp TO dukcapil_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dukcapil_user;

-- 10. Verify Setup
SELECT 'Dukcapil database setup completed successfully!' as status;
SELECT COUNT(*) as total_ktp_records FROM ktp_dukcapil;
SELECT nik, nama_lengkap, tempat_lahir, jenis_kelamin FROM ktp_dukcapil LIMIT 5;

-- 11. Show Summary
SELECT * FROM ktp_summary;

-- 12. Test Indexes
EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM ktp_dukcapil WHERE nik = '3175031234567890' AND LOWER(nama_lengkap) = LOWER('John Doe');

EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM ktp_dukcapil WHERE nik = '3175031234567890';

-- 13. Show Database Info
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats 
WHERE tablename = 'ktp_dukcapil' 
ORDER BY schemaname, tablename, attname;