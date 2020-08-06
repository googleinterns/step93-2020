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
