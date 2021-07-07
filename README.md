# Boomerang

## Installation
1. Download the latest clar file from https://github.com/swhitley/Boomerang/releases/latest.
2. Import the clar file into Workday Studio.
3. Deploy the integration to your Workday tenant.

## Launch Options

There are three separate options for launching Boomerang.

1. a - Event Document Name Contains - This is the direct replacement for the legacy `WebServiceRequester` integration. The parameter may be left blank when being used with an EIB, and the integration will pick up the deliverable document from the EIB.  If there are multiple deliverable documents, use this parameter to match and select the desired deliverable document for processing.
2. b - Input Document - A fully formed SOAP request document can be attached at runtime. The Boomerang integration can be run in standalone mode and does not need to be connected to an EIB.
3. c - Custom Report and c - Custom Transformation - Run the Boomerang as an standalone integration.  Select a Workday report and attach the XSLT document to be used to transform the report output. 

![image](https://user-images.githubusercontent.com/413552/124685009-44ba2a80-de85-11eb-9632-e48dec777cf7.png)
