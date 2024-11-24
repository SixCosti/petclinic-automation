# petclinic-automation

This repository serves as the source code and as the Development Environment for the Pet Clinic website. The main task is the automation of the deployment of such website to be easily launched for each future pet shop that they might open and also to ease the development method separately for each pet shop as there might be slight differences for each shop.

The client has an internal website for managing customers, pets and vetenarians data for their pet shop. However, soon they're planning in opening a few more pet shops under the same brand which means that the automation of the internal website is now needed more than ever. Setting up the website manually can become time consuming and as the business extends such easy deployment procedure would be beneficial. 

Holding the website on-premises means extra management procedures interms of security, hardware, storage and genral maintenance for each pet shop which would not be ideal. Holding each website using a Cloud Provider would be more cost-effcient and hassle free. Most Cloud providers offers great security tools and the hardware itself is handleld maintained by the Cloud Provider.

Their current website is based on a REST Angular and Spring methodologies. It has come to my attention that the "type of pet" section is missing from the Owners table, that would be a welcome adition as well.

* Development Step

By using Java as a general-purpose language, by understanding the current implementation and adding a new object to the owner's table the addition of the needed "type of pet" section can take place. New testing parameters should take place as well in order to verify that the code for the new feature is error free and robust.

Testing the application on the frontend and adding new front elements (if neccesary) will be also needed.

* Running the app, it is made by two commands
The backend which ensure that the database it's updated accordingly and the website funstion properly.

- JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw spring-boot:run
- JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 mvn clean install (for tests)

The frontend which involves the end-user experience and the UI user-friendly elements.

- ng build (once)
- ng serve
- ng test 

chrome env var neccesary: 
- export CHROME_BIN=/usr/bin/chromium






