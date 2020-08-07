# step93-2020: Capstone

Welcome to EpiCURE, a solution for restaurants struggling due to COVID-19!

#### To deploy this webapp to an App Engine project:

- Login to [Google Cloud Shell](https://ssh.cloud.google.com/cloudshell/editor)
- Clone this repo
- In your GCP project, set up a VM instance for elastic search use
  - Edit the [VPC access connector line in the `appengine-web.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/src/main/webapp/WEB-INF/appengine-web.xml#L12) to include your GCP project ID, location, and search connection
  - Edit the [`context-param` in `web.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/src/main/webapp/WEB-INF/web.xml) to be the name of your search instance
- Navigate into the `capstone` directory
  - Edit [line 146 of the `pom.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/pom.xml#L146) by replacing `PROJECT-ID-HERE` with the ID of your GCP project
  - Deploy necessary datastore indices by running `mvn appengine:deployIndex`
  - Deploy the project by running `mvn package appengine:deploy`
    - You may have to run `gcloud auth login` and/or `gcloud config set project YOUR-PROJECT-ID` before deploying

#### To seed data using the Remote API:

- Navigate into the `capstone` directory
  - Run `mvn install` to ready the dependency tree
- Navigate into the `remote` directory
  - Run `mvn clean package` to create a `jar` file
  - Run `cd target/` to navigate to the directory with the `jar`
  - Run `java -jar remote-1.0-SNAPSHOT-jar-with-dependencies.jar "YOUR-PROJECT-ID"`
  - Ensure that "success" is printed!


#### Google Cloud Scheduler
In order to implement the scheduler needed to update a restaurant's score, you
will need to complete the following steps:

1. Go to your deployed Google Cloud Project's dashboard.
2. Enter the naviation menu and loog for Cloud Scheduler under the Tools section.
3. Select 'Create Job'.
4. Complete all the fields needed to create the job.
    - Choose the name for the job.
    - Put the frequency as: "0 6 * * 7" (this means the job will run every Sunday at 6:00 AM).
    - Select your desired TimeZone.
    - The endpoint for the job is "/update-score" (that's what should go at the end of the rest of the URL for your site).
    - The method should be a POST request and body should be empty.
