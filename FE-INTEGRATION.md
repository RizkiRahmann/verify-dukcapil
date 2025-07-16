# 📝 Frontend Configuration Guide

## 🔧 Yang Perlu Diubah Kalau Link/Endpoint Berubah

### 1. **UTAMA: Ganti URL di EKTPVerificationPage.js**

**📍 Lokasi:** Line 21-22 di function `handleSubmit`

```javascript
// ===== GANTI BAGIAN INI =====
const response = await fetch(
  'https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/verify-nik',
  // ☝️ GANTI URL DI SINI
```

**🔄 Contoh Perubahan:**

#### Kalau Cloudflare tunnel baru:
```javascript
// DARI:
'https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/verify-nik'

// JADI:
'https://new-tunnel-url.trycloudflare.com/api/dukcapil/verify-nik'
```

#### Kalau balik ke localhost:
```javascript
// DARI:
'https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/verify-nik'

// JADI:
'http://localhost:8081/api/dukcapil/verify-nik'
```

#### Kalau production server:
```javascript
// DARI:
'https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/verify-nik'

// JADI:
'https://api.yourdomain.com/api/dukcapil/verify-nik'
```

---

### 2. **OPSIONAL: Update Debug Info (Line ~118)**

**📍 Lokasi:** Di bagian debug footer

```javascript
// ===== GANTI BAGIAN INI (OPSIONAL) =====
<small className="text-muted">
  🔗 API: vietnamese-engineer-intersection-handy.trycloudflare.com | 
  //      ☝️ GANTI URL DI SINI JUGA
</small>
```

---

## 🎯 **TEMPLATE untuk Copy-Paste**

### Template 1: Cloudflare Tunnel Baru
```javascript
const response = await fetch(
  'https://YOUR-NEW-TUNNEL-URL.trycloudflare.com/api/dukcapil/verify-nik',
  {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    },
    body: JSON.stringify({ 
      nik: nik.trim(), 
      namaLengkap: namaLengkap.trim(), 
      tanggalLahir 
    }),
  }
);
```

### Template 2: Localhost Development
```javascript
const response = await fetch(
  'http://localhost:8081/api/dukcapil/verify-nik',
  {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    },
    body: JSON.stringify({ 
      nik: nik.trim(), 
      namaLengkap: namaLengkap.trim(), 
      tanggalLahir 
    }),
  }
);
```

### Template 3: Production Server
```javascript
const response = await fetch(
  'https://api.yourcompany.com/api/dukcapil/verify-nik',
  {
    method: 'POST',
    headers: { 
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Authorization': 'Bearer YOUR-API-KEY' // Jika pakai auth
    },
    body: JSON.stringify({ 
      nik: nik.trim(), 
      namaLengkap: namaLengkap.trim(), 
      tanggalLahir 
    }),
  }
);
```

---

## 🔧 **Advanced: Environment Variables (Recommended)**

### 1. Buat file `.env` di root frontend project:
```env
REACT_APP_DUKCAPIL_API=https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil
```

### 2. Update EKTPVerificationPage.js:
```javascript
// ===== GANTI STATIC URL DENGAN ENV VARIABLE =====
const API_BASE_URL = process.env.REACT_APP_DUKCAPIL_API || 'http://localhost:8081/api/dukcapil';

const response = await fetch(
  `${API_BASE_URL}/verify-nik`,
  // ... rest of config
);
```

### 3. Different environments:
```bash
# .env.development
REACT_APP_DUKCAPIL_API=http://localhost:8081/api/dukcapil

# .env.staging  
REACT_APP_DUKCAPIL_API=https://staging-tunnel.trycloudflare.com/api/dukcapil

# .env.production
REACT_APP_DUKCAPIL_API=https://api.production.com/api/dukcapil
```

---

## ⚡ **Quick Change Checklist**

### ✅ **Kalau Cloudflare Tunnel Expired/Berubah:**
1. [ ] Copy tunnel URL baru dari terminal backend
2. [ ] Ganti URL di line ~22 `EKTPVerificationPage.js`
3. [ ] (Opsional) Update debug info di line ~118
4. [ ] Save file → frontend auto-reload
5. [ ] Test dengan sample data

### ✅ **Kalau Ganti ke Localhost:**
1. [ ] Ganti URL ke `http://localhost:8081/api/dukcapil/verify-nik`
2. [ ] Pastikan backend service running
3. [ ] Test connection

### ✅ **Kalau Deploy Production:**
1. [ ] Setup domain/server untuk backend
2. [ ] Ganti URL ke production domain
3. [ ] Tambah HTTPS dan auth headers jika perlu
4. [ ] Update CORS di backend untuk production domain

---

## 🚨 **Common Issues & Fixes**

### Issue 1: "Fetch Failed" / Connection Error
```javascript
// CHECK: Apakah URL benar?
console.log('Testing URL:', 'https://your-tunnel.trycloudflare.com/api/dukcapil/ping');

// TEST via browser:
// https://your-tunnel.trycloudflare.com/api/dukcapil/health
```

### Issue 2: CORS Error
```javascript
// REMOVE credentials jika error:
// credentials: 'include', // ← COMMENT/HAPUS INI

// ATAU tambah headers:
headers: { 
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Origin': window.location.origin  // ← TAMBAH INI
}
```

### Issue 3: 404 Not Found
```javascript
// CHECK endpoint path:
'/api/dukcapil/verify-nik'  ✅ BENAR
'/api/verification/nik'     ❌ SALAH (endpoint lama)
'/dukcapil/verify-nik'      ❌ SALAH (missing /api)
```

---

## 📋 **File Locations Summary**

```
frontend-project/
├── src/
│   ├── components/
│   │   └── EKTPVerificationPage.js  ← MAIN FILE TO EDIT
│   └── ...
├── .env                             ← ENV VARIABLES (optional)
├── .env.development                 ← DEV ENV (optional)
└── .env.production                  ← PROD ENV (optional)
```

---

## 🎯 **One-Liner Commands untuk Test**

```bash
# Test tunnel health
curl https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/ping

# Test verification dengan sample data
curl -X POST "https://vietnamese-engineer-intersection-handy.trycloudflare.com/api/dukcapil/verify-nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"3175031234567890","namaLengkap":"John Doe","tanggalLahir":"1990-05-15"}'

# Test localhost
curl http://localhost:8081/api/dukcapil/ping
```

---

## 💡 **Pro Tips**

1. **Always test API first** dengan curl sebelum update frontend
2. **Use environment variables** untuk easy switching
3. **Keep debug info** sampai production
4. **Save working URLs** di notepad untuk backup
5. **Test dengan sample data** setelah setiap perubahan

**Intinya: Cuma perlu ganti 1 line URL di EKTPVerificationPage.js! 🎯**