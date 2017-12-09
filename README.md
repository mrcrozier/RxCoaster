
![](https://imgur.com/7OlUAY1.jpg)
## **RxCoaster**- The reactive android component of our UCF Senior Design project, Clever Coasters

Designed to run in the background while the restaurant has their own Point of Sale application running in the foreground, our application connects to a coaster device by tapping the coaster to the NFC-enabled Android device. Stored in this device is information about the coaster- its database ID, bluetooth address, GATT notification UUID, etc. Using NFC, the app then connects to the Bluetooth low energy enabled microcontroller inside the coaster with the given information, and starts getting updates on the state of the coaster. If the state changes, the app fires off a put request to our REST API with the updated status, which automatically updates the webpage so waiters can see at a glance what table needs servicing. 


![](https://imgur.com/0qNEvlZ.png)

[The code for the REST API and webpage can be found here](https://github.com/mrcrozier/SD2-Webpage). 

Contact me at mrcrozier@gmail.com if you want to know more

Special thanks to [RxJava](https://github.com/ReactiveX/RxJava), [Retrofit](https://github.com/square/retrofit), and especially [Blueteeth](https://github.com/RobotPajamas/Blueteeth) for making life easier when developing this app