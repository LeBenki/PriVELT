# PriVELT

Android application to centralise known data from different services. The project is funded by PriVELT (https://privelt.ac.uk/).
PriVELT aims to make travellers aware of the risks of the Internet. The application collect all private data known by the different services (see Services supported),
to inform the user and help him to reduce his privacy risks. PriVELT also monitor local installed applications by checking all the permissions they are asking for,
and which permissions are granted by the user. PriVELT does not have a remote server, so all the information is hold locally on the device user.
The local database is secured with a password known and chosen only by the user. PriVELT also have scores and charts to help the user to understand the privacy issues he is facing.

## Screenshots

![Master password](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/1.jpg "Optional Title") ![Save database in another service](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/4.jpg "Optional Title")
![Service and data cards](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/2.jpg "Optional Title") ![Granted permissions](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/5.jpg "Optional Title")
![Permissions cards](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/3.jpg "Optional Title") ![Details of permission type](https://raw.githubusercontent.com/LeBenki/PriVELT/dev/screenshots/6.jpg "Optional Title")

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:LeBenki/PriVELT.git
```

To test the app you will need a github.properties file in the root of the project with :

```
gpr.usr=GITHUB USERNAME
gpr.key=GITHUB TOKEN OR PASSWORD
```

It is required to use the PDA wrapper library

## Services supported

Location history:
  - Google.com (Maps)

Travelling Websites:
  - Hotels.com
  - Booking.com
  - Expedia.com
  - Agoda

Online Social Networks:
  - Twitter
  - Facebook
  - Instagram
  - Pinterest

## Maintainers

The project is maintained by:
* [Lucas Benkemoun](http://github.com/LeBenki)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request

## License

MPL-2.0
