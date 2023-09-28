# GUIChat
GUIChat er et Chat Client/Server program, hvor man kan forbinde til en server med forskellige klienter via sockets.
Vil du kører programmet på localhost skal du bare kører starte serveren og derefter starte Main, hvor GUI vil starte.
Vil du derimod kører programmet over netværket, skal du ændre klient klassens IP adresse, til serverens ip adresse.

For at tilslutte til chatten, skal du vælge et brugernavn der ikke er taget, ved at skrive !navn navn.
For at få listen over de brugere der er tilsluttet chatten skal du skrive !brugere.
For at sende en privat besked, skal du indtaste @navn besked.
Hvis du ønsker at skifte navn, kan du skrive !navn navn og chatten vil bekræfte dit nye brugernavn er tilsluttet.

Serveren holder en liste over aktive brugere i chatten, når en bruger skriver en besked broadcaster serveren beskeden til de aktive brugere.
Når en bruger tilslutter eller forlader chatten, broadcaster serveren en besked, så alle kan se hvem der er tilsluttet eller har forladt chatten.
Hvis du tilslutter chatten og der allerede er nogle andre der har skrevet beskeder, vil du få vist de 5 seneste beskeder. 
