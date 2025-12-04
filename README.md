# Jalgpalli Ruumi Broneerimissüsteem

Lihtne veebirakendus, kus kasutajad saavad broneerida jalgpallisaali aegu.
Projekt on tehtud meeskonnatööna kooli jaoks.


## Mida see teeb?

- Kasutajad saavad vaadata kalendrit ja näha, millised ajad on juba broneeritud
- Saab broneerida vabu aegu (nt 10:00-11:00)
- Broneeringu saab tühistada, aga ainult siis kui on veel vähemalt 24 tundi aega
- Kõik näevad kõiki broneeringuid

## Tehnoloogiad

- **Java 17** - programmeerimiskeel
- **Spring Boot** - raamistik, mis teeb asjad lihtsamaks
- **H2 Database** - andmebaas (ajutine, ainult testimiseks)
- **Maven** - dependency haldus
- **Lombok** - vähendab koodi kirjutamist

## Kuidas käivitada

### 1. Vaja on:
- Java 17 või uuem
- Maven
- IntelliJ IDEA (soovituslik)

### 2. Projekti käivitamine:
bash
# Klooni projekt
git clone https://github.com/GreenBean0303/booking-calendar.git
cd booking-calendar

# Käivita
mvn spring-boot:run


### 3. Ava brauser:

http://localhost:8080


### 4. Vaata andmebaasi (H2 Console):

http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:calendardb
Username: sa
Password: (jäta tühjaks)

Või kasuta mysql'i:
kasutaja: calendar_user
parool:calendar_pass


## Projekti struktuur

src/main/java/com/spordi/calendar/
├── model/              # Andmemudelid (User, Booking)
├── repository/         # Andmebaasiga suhtlemine
├── service/            # Äriloogika (reeglid, valideerimised)
├── controller/         # REST API endpoint'id
├── dto/                # Andmete edastamiseks
├── exception/          # Veahaldus
└── config/             # Seadistused


### Mis iga kiht teeb?

**Model** - Kirjeldab, kuidas andmed välja näevad (kasutajad, broneeringud)

**Repository** - Teeb päringuid andmebaasi (salvesta, leia, kustuta)

**Service** - Kontrollib reegleid:
- Kas aeg on tulevikus?
- Kas aeg pole juba broneeritud?
- Kas 24h on veel aega tühistamiseks?

**Controller** - Võtab vastu HTTP päringud ja tagastab vastused

## API Näited

### Loo broneering
```bash
POST /api/bookings
Headers: User-Id: 1, Content-Type: application/json

{
  "roomName": "Jalgpalliruum",
  "startTime": "2025-12-25T10:00:00",
  "endTime": "2025-12-25T11:00:00"
}


### Vaata kõiki broneeringuid

GET /api/bookings


### Tühista broneering

DELETE /api/bookings/1
Headers: User-Id: 1


## Reeglid (äriloogika)

1. **Aeg peab olema tulevikus** - ei saa minevikku broneerida
2. **Aeg ei tohi olla broneeritud** - sama aeg ei saa olla kahel inimesel
3. **24h tühistamise reegel** - saad tühistada ainult siis, kui on veel vähemalt 24h enne algust

## Testid
bash


## Meeskond

- **Agnes** - Backend, Repository, Service
- **Artur** - Controller, API
- **Fredy** - Testid, dokumentatsioon


## Litsents

See on õppeprojekt, kasuta vabalt.