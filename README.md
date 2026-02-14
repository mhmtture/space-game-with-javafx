🚀 Space Invaders (JavaFX)
Bu proje, JavaFX kütüphanesi kullanılarak geliştirilmiş, klasik atari oyunlarından esinlenen bir uzay savaşı oyunudur. Oyuncu, kendi uzay gemisini kontrol ederek düşman istilacıları yok etmeye ve hayatta kalmaya çalışır.

🎮 Oyun Özellikleri
Akıcı Hareketler: Klavye kontrolleri ile hassas gemi yönetimi.

Görsel Varlıklar: assets klasöründe yer alan özelleştirilmiş gemi ve düşman grafikleri.

Puan Sistemi: Yok edilen her düşman için artan skor tablosu.

Dinamik Zorluk: Oyun ilerledikçe artan heyecan.

🛠️ Kullanılan Teknolojiler
Dil: Java

Arayüz: JavaFX

Grafikler: PNG/JPG formatında oyun içi varlıklar

📂 Dosya Yapısı
Proje hiyerarşisi aşağıdaki gibidir:

Plaintext
space-game-with-javafx/
└── src/
    ├── assets/             # Oyun içi resimler, ikonlar ve arka planlar
    └── SpaceInvader.java   # Oyunun ana mantığı ve JavaFX kodu
🚀 Kurulum ve Çalıştırma
Projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyebilirsiniz:

Gereksinimler
JDK 11 veya daha üzeri bir sürüm.

JavaFX SDK (Eğer JDK içerisinde dahil değilse).

🏠 Ana Menü Kontrolleri
ENTER: Oyunu başlatır.

ESC: Uygulamadan tamamen çıkar.

🎮 Oyun İçi Kontroller
SOL OK (Left Arrow): Uzay gemisini sola hareket ettirir.

SAĞ OK (Right Arrow): Uzay gemisini sağa hareket ettirir.

BOŞLUK (Space): Ateş eder.

Not: Ateş etmenin kısa bir bekleme süresi (cooldown) vardır.

Power-up Aktifse: Aynı anda hem düz hem de iki çapraz yöne mermi atar.

P Tuşu: Oyunu duraklatır (Pause) veya devam ettirir. Duraklatıldığında düşman üretimi de durur.

ESC: Oyunu durdurur ve ana menüye döner.

💀 Game Over (Oyun Bitti) Ekranı
R Tuşu: Oyunu en baştan yeniden başlatır.

ESC: Skorunuzu gördükten sonra ana menüye geri döner.

💡 Koddan Küçük Notlar
Güçlendirici (Reward): Yeşil bir ödül topladığında 5 saniye boyunca 3'lü ateş etme yeteneği kazanırsın.

Ceza (Punishment): Kırmızı cezaya çarparsan skorun 50 puan düşer.

Puan: Vurduğun her düşman sana 100 puan kazandırır; bazen şansına ekstra +50 puan düşebilir.
