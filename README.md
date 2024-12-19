# petclinic-automation

## End User

This is the source code for the internal Pet Clinic management System Website, it contains both the Development and the Deployment configurations.

*What's New*

Due to high demand and to reach high standards for the business needs, a new feature has been added to the website: `Breed`.
This feature was requested and implemented to help the veterinary practice prepare in a more precise manner before the appointments as different breeds of pets will need a diferent dosage of medicine and different treatments.
Simply categorising the pets, for example, just as `dog` or `cat` or `bird` has been proven to be inaccurate and lead to last-minute treatment change.

With this new feature which can be found on the top left tab, the staff can add the breeds that are can be treated by the veterinary surgeons.
Furthermore, when adding a pet entry to an Owner, besides of the usual mandatory options such as `Type of Pet`, the `Breed` feature is also avilable; as a dropdown list.


![image](https://github.com/user-attachments/assets/92a6d026-0de9-4062-9693-83f430b42000)



*Known Limitations*

At the moment, the `Breed` functions in a similar way as `Type of Pet`, it can be created individually and the entries can be selected from the list when adding a `Pet` entity to an `Owner` entry.
As an example, if an Owner has a pet type `dog`, when selecting the breed, it will display teh entire collection in the list, that means that `cat` breeds and other breeds can be selected as well.

A great enhancement to ensure a more user-friendly approach and to minimise possible errors and discomfort would be making the "Breed" feature depend on what `Type of Pet` is being selected.
For example if selecting a `dog` type of pet, then the `breed` dropdown list will only show related entries and not all of the available breeds at once.

However, due to time constrains the feature has been deployed with the core attributes for now.

Not to worry, due to the recent DevOps values and CI/CD integrations, now deploying such chnages is automated and the developer team can focus on what they do best and worry less about deploying new features or issue fixes.
This would mostly mean that Support tickets and Feedback for improvments will take place in a more timely manner than before.

Another discomfort is relying on the server's IP to access the website, due to the close deadline and resources limitations, a last minute Elastic IP or DNS mask could not be provided in time to offer a more user friendly address yet.


## Developer 

*Overview*

This is the source code for the internal website for managing customers, pets and vetenarians data for the pet shop. Its automation of the internal website is now needed more than ever. Setting up the website manually can become time consuming and as the business extends such easy deployment procedure would be beneficial. 

Holding the website on-premises means extra management procedures interms of security, hardware, storage and genral maintenance for each pet shop which would not be ideal. Holding each website using a Cloud Provider would be more cost-effcient and hassle free. Most Cloud providers offers great security tools and the hardware itself is handleld maintained by the Cloud Provider.

The website is based on a REST Angular and Spring methodologies.

By using Java as a general-purpose language, by understanding the current implementation and adding a new object to the owner's table the addition of the needed "breeds" section has been applied. New testing parameters should took place as well in order to verify that the code for the new feature is error free and robust.

![petclinic-ermodel](https://github.com/user-attachments/assets/fe595d09-014b-47a5-b13c-4bf866689592)


Testing the application on the frontend and adding new front elements was also needed.

* Running the app, it is made by two commands
The backend which ensure that the database it's updated accordingly and the website funstion properly.

- JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw spring-boot:run
- JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 mvn clean install (for tests)

The frontend which involves the end-user experience and the UI user-friendly elements.

- ng build -configuration-production (build the app ready for production)
- ng serve - launch it locally (great for development)
- ng test  - run tests (browser executable path needed/can also be run headless)

chrome env var neccesary: 
- export CHROME_BIN=/usr/bin/chromium

More information about how these work can be found in the original author guides within the `rest` and `angular` directory respectively.

*What the CI/CD change brings to the table*

After picking up a task and pulling down the source code for development please be aware of the new "GitFlow" branching strategy.
It reduces merge conflict and encourages pair programming and working as a team, a simple diagram can be seen bellow:

```
  * master (production) <(!) Live Environment>
  |
  |--> acceptance (staging/pre-prod) < Changes are tested in an isolated Environment >
  |    |
  |    |--> dev (development) < Code Review >
  |         |
  |         |--> feature branches (individual tasks or features) < Developer's SandBox >
```

After the new changes are approved through all three stages, when reaching the `main` branch a GitHook will be triggered resulting in the pipeline's execution (Jenkins).
This will do thge following:

- Unit and Integration tests on both Frontend and Backend.
- If succesful, those will be containerized and pushed to Docker.
- Terraform will take care of the infrastructure such as the EC2 instance that holds the website server, RDS MySQL db that holds the data, security groups etc.
- It will also set up Amazon CloudWatch for monitoring the CPU and Storage usage of the instances, alerts are taking place if certain limits are reached.
- Ansible will configure the resources, it will set up the kubernetes to pull the Docker images from earlier and orchestrate them keeping the application up and running.
- An OWASP Zap security scan will take place to ensure no Critical vulnerbilities have been regressed.



![Diagram](https://github.com/user-attachments/assets/4699637e-1380-4dab-8b94-b6a6343aa8df)




*Costs*

Since the budget was quite limited, the most cost-efficient procedures had to take place.
The app is lightweight and does not expect a lot of traffic since it's only used internally by the staff so no problem with that.

The Cloud Provider used is AWS.
- The main server it's a t2.Medium EC2 instance with 4 cores CPU and 4GB of RAM, lightweight kubernets has been implemented to ensure enough room fo the app itself to run.
- Instead of setting up kubeadm with two of such instance (one control-plane, one worker), k3s is used instead.
- The RDS instance is free tier and can be used 750 hours/month for a year, it comes with a 20GB volume which should be enough, however, it can be scaled as much as the business needs.
- CloudWatch it's also Free tier as long as no more than 10 Alerts are set up and no more than 1Mil API calls
The first year cost estimate should be around £120.00, which should give plenty of room for the organisation to take measurements and upgrade as needed.



