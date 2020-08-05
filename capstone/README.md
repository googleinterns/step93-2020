This directory is where you'll write all of your code!

By default it contains a barebones web app. To run a local server, execute this
command:

```bash
mvn package appengine:run
```

#Scheduler

In order to implement the scheduler needed to update a restaurant's score, you
will need to complete the following steps:

1. Go to your deployed Google Cloud Project's dashboard.
2. Enter the naviation menu and loog for Cloud Scheduler under the Tools
   section.
3. Select 'Create Job'.
4. Choose the name for the job, put the frequency as: "0 6 * * 7" (this means
   the job will run every Sunday at 6:00 AM), select your desired TimeZone, the
   endpoint for the job is "/update-score" (that's what should go at the end of
   the rest of the URL for your site), the method should be a POST request and
   body should be empty.
