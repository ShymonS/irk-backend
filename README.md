Wymagania wstępne
Zanim rozpoczniesz, upewnij się, że masz zainstalowane na swoim komputerze:

Java 17 (lub nowsza)

Maven (jeśli nie używasz wbudowanego w środowisko programistyczne)

Node.js (wersja 16+ lub 18+) oraz menedżer pakietów npm

Skonfigurowaną i uruchomioną bazę danych (np. MySQL lub PostgreSQL, zgodnie z konfiguracją w pliku application.properties)

1. Uruchomienie Backendu (Spring Boot)
Backend aplikacji działa domyślnie na porcie 8081. Wraz z pierwszym uruchomieniem system automatycznie utworzy tabele w bazie danych oraz wygeneruje domyślne konta testowe.

Opcja A: Przez środowisko IDE (IntelliJ IDEA / Eclipse)
Otwórz folder z backendem w swoim środowisku programistycznym.

Poczekaj na pobranie zależności przez Mavena.

Znajdź główną klasę aplikacji (np. IrkBackendApplication.java).

Uruchom metodę main.

Opcja B: Przez terminal
Otwórz terminal i przejdź do katalogu głównego backendu:

Bash
cd sciezka/do/backendu
Zbuduj projekt i pobierz zależności:

Bash
mvn clean install
Uruchom aplikację:

Bash
mvn spring-boot:run
Po poprawnym uruchomieniu w konsoli powinien pojawić się komunikat o działaniu na porcie 8081 oraz informacje od narzędzia DataSeeder o utworzeniu kont.

2. Uruchomienie Frontendu (React)
Frontend aplikacji komunikuje się z backendem i działa domyślnie na porcie 5173.

Otwórz nowe okno terminala Node i przejdź do katalogu z frontendem:

Bash
cd sciezka/do/frontendu
Zainstaluj wszystkie wymagane pakiety:

Bash
npm install
Uruchom serwer deweloperski:

Bash
npm run dev
(Jeśli projekt był tworzony przez Create React App, użyj polecenia npm start)

Otwórz przeglądarkę i wejdź pod adres wskazany w terminalu (np. http://localhost:5173).

Zaseedowane konto Administratora:

Email: admin@admin.com

Hasło: admin123