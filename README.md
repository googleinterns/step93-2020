# EpiCURE: STEP Capstone Project

Welcome to EpiCURE, a solution for restaurants struggling due to COVID-19!

## To deploy this webapp to an App Engine project:
- Login to [Google Cloud Shell](https://ssh.cloud.google.com/cloudshell/editor)
- Clone this repo
- In your GCP project, set up a [VM instance for Elasticsearch](#elasticsearch-setup)
- Navigate into the `capstone` directory
  - Edit [line 146 of the `pom.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/pom.xml#L146) by replacing `PROJECT-ID-HERE` with the ID of your GCP project
  - Deploy necessary Datastore indices by running `mvn appengine:deployIndex`
  - Deploy the project by running `mvn package appengine:deploy`
    - You may have to run `gcloud auth login` and/or `gcloud config set project YOUR-PROJECT-ID` before deploying

### Elasticsearch Setup
In order to set up Elasticsearch to work with App Engine, a server running Elasticsearch must be set up. The following information will be for if the server is set up to run run on Google Compute Engine.

#### Creating an Elasticsearch Server to Run with Google Compute Engine

1. [Create a VM Instance](https://cloud.google.com/compute/docs/instances/create-start-instance)
2. Install Elasticsearch
3. Go to the directory in which Elasticsearch was installed
4. Open `./elasticsearch.yml`
5. Change the settings according to the guide at [GCE Discovery Plugin](https://www.elastic.co/guide/en/elasticsearch/plugins/7.8/discovery-gce.html)

#### Connecting to VM instance
1. [Configure the Elasticsearch VM instance for Serverless VPC Access](https://cloud.google.com/vpc/docs/configure-serverless-vpc-access#connectors)
2. [Set up App Engine to use the VPC Access connector](https://cloud.google.com/appengine/docs/standard/java/connecting-vpc)
    - Edit the [`vpc-acccess-connector` tag in `appengine-web.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/src/main/webapp/WEB-INF/appengine-web.xml#L12) to include your GCP project ID, location, and search connection
3. Edit the [`context-param` tag in `web.xml`](https://github.com/googleinterns/step93-2020/blob/master/capstone/src/main/webapp/WEB-INF/web.xml) to be the hostname of your VM instance
	- To get the hostname of the VM instance, SSH into the instance and type `hostame -f` into the prompt.

## To seed data using the Remote API:
- Navigate into the `capstone` directory
  - Run `mvn install` to ready the dependency tree
- Navigate into the `remote` directory
  - Run `mvn clean package` to create a `jar` file
  - Run `cd target/` to navigate to the directory with the `jar`
  - Run `java -jar remote-1.0-SNAPSHOT-jar-with-dependencies.jar "YOUR-PROJECT-ID"`
  - Ensure that "success" is printed!


## Google Cloud Scheduler
In order to implement the scheduler needed to update a restaurant's score, you
will need to complete the following steps:

1. Go to your deployed Google Cloud Project's dashboard.
2. Enter the navigation menu and look for Cloud Scheduler under the Tools section.
3. Select 'Create Job'.
4. Complete all the fields needed to create the job.
    - Choose the name for the job.
    - Put the frequency as: "0 6 * * 7" (this means the job will run every Sunday at 6:00 AM).
    - Select your desired TimeZone.
    - The endpoint for the job is "/update-score" (that's what should go at the end of the rest of the URL for your site).
    - The method should be a POST request and body should be empty.
