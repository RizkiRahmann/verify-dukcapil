# Dukcapil KTP Service - API Contract

## Service Information
- **Service Name:** Dukcapil KTP Verification Service
- **Version:** 1.0.0
- **Base URL:** `http://localhost:8081/api/dukcapil`
- **Protocol:** HTTP/HTTPS
- **Content-Type:** `application/json`

## Authentication
- **Type:** None (Development)
- **Production:** API Key Header (future implementation)

---

## Endpoints

### 1. **POST** `/verify-nik` ⭐ **MAIN ENDPOINT**
Verifikasi NIK dengan nama lengkap dan tanggal lahir (Enhanced)

#### Request Body
```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe",
  "tanggalLahir": "1990-05-15"
}
```

#### Validation Rules
- `nik`: Required, exactly 16 digits, numeric only
- `namaLengkap`: Required, 2-100 characters
- `tanggalLahir`: Required, format YYYY-MM-DD, tidak boleh masa depan

#### Success Response (200)
```json
{
  "valid": true,
  "message": "Data NIK, nama, dan tanggal lahir valid sesuai database Dukcapil",
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "agama": "Islam",
    "statusPerkawinan": "BELUM KAWIN"
  },
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}
```

#### Detailed Error Messages
```json
// NIK tidak terdaftar
{
  "valid": false,
  "message": "NIK tidak terdaftar di database Dukcapil",
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}

// NIK dan nama cocok, tanggal lahir tidak
{
  "valid": false,
  "message": "NIK dan nama sesuai, namun tanggal lahir tidak cocok. Tanggal lahir di database: 1990-05-16",
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}

// NIK dan tanggal lahir cocok, nama tidak
{
  "valid": false,
  "message": "NIK dan tanggal lahir sesuai, namun nama tidak cocok. Nama di database: Jane Smith",
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}

// Semua data tidak cocok
{
  "valid": false,
  "message": "NIK terdaftar namun nama dan tanggal lahir tidak sesuai dengan data Dukcapil",
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}
```

#### Error Response (400)
```json
{
  "valid": false,
  "message": "Format NIK tidak valid. NIK harus 16 digit angka.",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

### 2. **POST** `/verify-nik-basic` 
Verifikasi NIK dengan nama lengkap saja (Legacy/Backward Compatibility)

#### Request Body
```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe"
}
```

#### Success Response (200)
```json
{
  "valid": true,
  "message": "Data NIK dan nama valid sesuai database Dukcapil",
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki"
  },
  "timestamp": "2025-07-16T10:30:00Z",
  "service": "Dukcapil Service"
}
```

---

### 3. **POST** `/check-nik`

---

### 3. **POST** `/check-nik`
Check keberadaan NIK tanpa validasi nama dan tanggal lahir

#### Request Body
```json
{
  "nik": "3175031234567890"
}
```

#### Success Response (200)
```json
{
  "exists": true,
  "nik": "3175031234567890",
  "message": "NIK terdaftar di database Dukcapil",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

### 4. **GET** `/health`
Check keberadaan NIK tanpa validasi nama

#### Request Body
```json
{
  "nik": "3175031234567890"
}
```

#### Success Response (200)
```json
{
  "exists": true,
  "nik": "3175031234567890",
  "message": "NIK terdaftar di database Dukcapil",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

### 3. **GET** `/health`
Health check service

#### Response (200)
```json
{
  "status": "OK",
  "service": "Dukcapil KTP Verification Service",
  "version": "1.0.0",
  "port": 8081,
  "database": "dukcapil_ktp",
  "totalRecords": 22,
  "endpoints": {
    "root": "GET /dukcapil/",
    "verifyNik": "POST /dukcapil/verify-nik",
    "checkNik": "POST /dukcapil/check-nik",
    "getKtpData": "GET /dukcapil/ktp-data/{nik}",
    "stats": "GET /dukcapil/stats",
    "health": "GET /dukcapil/health"
  },
  "timestamp": "2025-07-16T10:30:00Z",
  "uptime": 3600000
}
```

---

### 4. **GET** `/stats`
Statistik data KTP

#### Response (200)
```json
{
  "totalKtpRecords": 22,
  "service": "Dukcapil KTP Verification Service",
  "status": "Active",
  "timestamp": 1721120400000,
  "version": "1.0.0",
  "totalLaki-laki": 12,
  "totalPerempuan": 10
}
```

---

### 5. **GET** `/ktp-data/{nik}`
Get data KTP berdasarkan NIK (admin/debugging)

#### Path Parameters
- `nik`: string (16 digits)

#### Success Response (200)
```json
{
  "found": true,
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "agama": "Islam"
  },
  "message": "Data KTP ditemukan",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

### 6. **GET** `/ping`
Quick health check

#### Response (200)
```json
{
  "message": "pong",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

## Error Handling

### Common Error Responses

#### 400 - Bad Request
```json
{
  "valid": false,
  "message": "NIK wajib diisi",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

#### 500 - Internal Server Error
```json
{
  "valid": false,
  "message": "Terjadi kesalahan sistem: Database connection failed",
  "service": "Dukcapil Service",
  "timestamp": "2025-07-16T10:30:00Z"
}
```

---

## Sample Usage

### Java Client Example
```java
// Enhanced Verification dengan tanggal lahir
NikVerificationRequest request = new NikVerificationRequest(
    "3175031234567890", 
    "John Doe",
    LocalDate.of(1990, 5, 15)
);

RestTemplate restTemplate = new RestTemplate();
ResponseEntity<Map> response = restTemplate.postForEntity(
    "http://localhost:8081/api/dukcapil/verify-nik",
    request,
    Map.class
);

boolean isValid = (Boolean) response.getBody().get("valid");

// Basic Verification (Legacy)
Map<String, String> basicRequest = Map.of(
    "nik", "3175031234567890",
    "namaLengkap", "John Doe"
);

ResponseEntity<Map> basicResponse = restTemplate.postForEntity(
    "http://localhost:8081/api/dukcapil/verify-nik-basic",
    basicRequest,
    Map.class
);
```

### cURL Example
```bash
# Enhanced Verification dengan tanggal lahir
curl -X POST http://localhost:8081/api/dukcapil/verify-nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tanggalLahir": "1990-05-15"
  }'

# Basic Verification (Legacy)
curl -X POST http://localhost:8081/api/dukcapil/verify-nik-basic \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe"
  }'

# Check NIK existence
curl -X POST http://localhost:8081/api/dukcapil/check-nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890"
  }'

# Health check
curl http://localhost:8081/api/dukcapil/health
```

---

## Integration Notes

### CORS Configuration
- Allowed Origins: `localhost:8080`, `localhost:3000`, `localhost:5173`
- Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- Allowed Headers: `Content-Type`, `Authorization`, `X-API-Key`

### Security Headers
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `X-Service: Dukcapil-KTP-Service`
- `Cache-Control: no-cache, no-store, must-revalidate`

### Rate Limiting
- Currently: No rate limiting (development)
- Production: TBD

---

## Data Models

### Enhanced NIK Verification Request
```json
{
  "nik": "string (16 digits)",
  "namaLengkap": "string (2-100 chars)",
  "tanggalLahir": "string (YYYY-MM-DD format)"
}
```

### Basic NIK Verification Request (Legacy)
```json
{
  "nik": "string (16 digits)", 
  "namaLengkap": "string (2-100 chars)"
}
```

### NIK Validation Rules
- Length: Exactly 16 characters
- Format: Numeric only (0-9)
- Province code: First 2 digits ≠ "00"
- City code: Digits 3-4 ≠ "00"
- District code: Digits 5-6 ≠ "00"

### Name Validation Rules
- Length: 2-100 characters
- Case insensitive matching
- Trimmed whitespace

### Birth Date Validation Rules
- Format: YYYY-MM-DD (ISO 8601)
- Cannot be in the future
- Must be a valid date

---

## Environment Configuration

### Development
- Port: `8081`
- Database: `postgresql://localhost:5432/dukcapil_ktp`
- Security: Disabled
- CORS: Permissive

### Production (Future)
- API Key authentication
- Rate limiting
- Restricted CORS
- HTTPS only