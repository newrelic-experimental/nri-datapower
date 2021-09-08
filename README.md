[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)

# [nri-datapower]

The New Relic infrastructure integration for Datapower monitors IBM datapower devices by periodically querying the XML management interface and reporting metric data into New Relic.

## Installation

- Create a folder {nri-datapower} and unzip the contents of nri-datapower.zip into it
- Copy datapower-config.yml.sample to datapower-config.yml
- Copy newrelic-insights.yml.sample to newrelic-insights.yml
- Enter datapower device connection information in the ***datapower-config.yml*** file. Create as many instances as needed and enter the datapower host, port, username, password for each datapower device to connect and query for metric data. Also enter the comma separated list of datapower domains to query for domain metrics.
- Enter the newrelic API key for the insights_insert_key property and update the account ID for the collector_url property in the ***newrelic-insights.yml*** (This is only needed for running the monitor in standalone mode- that is, not as a NewRelic infrastructure integration). If applicable, also uncomment and enter the HTTP proxy information.
- Edit the JAVA_HOME and APP_HOME properties in the following two scripts in the ***scripts/*** folders
	1. install-datapower-certificate.sh
	2. start.sh
- Execute the script for downloading and installing the datapower server certificate into the JRE trusted certificate store
	```cmd 
	install-datapower-certificate.sh {datapower-host:port} {jre-keystore-passphrase}
	```

## Getting Started (Standalone Mode)

- Execute the start script to start the nri-datapower monitor in standalone mode
	```cmd 
	start.sh 
	```
	
## Getting Started (New Relic Infrastructure Integration)




## Support

New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

>We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.



## Contributing

We encourage your contributions to improve [Project Name]! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project. If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company, please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License

[nri-datapower] is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

>[If applicable: [nri-datapower] also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.]
