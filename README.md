# Boomerang

A boomerang integration exports data out of Workday, transforms the data, and then feeds the data back to Workday to perform an update (like a boomerang <img src="https://user-images.githubusercontent.com/413552/129063819-869e47d6-b847-4f59-9f14-76ddb7539880.png" width="16" />
).

A Workday boomerang integration has three components:
1. A Workday report (RaaS) to extract data from Workday.
2. An XSLT file to transform the data into a web service request.
3. A Workday Studio application that takes the web service request and calls Workday's API to perform a data update.

This application, `Boomerang`, is a Workday Studio integration that can be run without any code changes. Simply deploy Boomerang and launch a boomerang integration with a report and an XSLT file.

- Boomerang can be run as part of an EIB, or it can be launched as a standalone integration. 
- Messages are logged using multiple output options, including Cloud Logs.

Use launch option `c` (below) as the quickest way to get started with Boomerang. 
1. Select the report that will serve as the boomerang input, **c - Custom Report**.  
2. Attach the XSLT document using **c - Custom Transformation**.
3. Select the `Web Service` that corresponds to the web service request in your XSLT.

**Reminder:**  The namespace in your XSLT (example: the text following `xmlns:wd=`) must match the namespace on your Workday report (under `Web Service Options`).

**Tip:** If you always override your Workday report's namespace with `urn:com.workday/bsvc` (instead of the Workday-generated namespace), you'll be less likely to have a mismatch in your XSLT.

## Installation
1. Download the latest clar file from https://github.com/swhitley/Boomerang/releases/latest.
2. Import the clar file into Workday Studio.
3. Deploy the integration to your Workday tenant.

## Launch Options

There are three separate options for launching Boomerang. When selecting an option (a, b, or c), you must leave the parameters for the other options blank.

1. **a - Event Document Name Contains** (use with Outbound EIBs) - This is the direct replacement for the legacy `WebServiceRequester` integration. The parameter may be left blank when being used with an EIB; the integration will pick up the deliverable document from the EIB output.  If there are multiple deliverable documents, use this parameter to match the name of the desired deliverable document.
2. **b - Input Document** (use with a document that has already been transformed to a request) - A fully formed SOAP request document can be attached at runtime. For this option, the boomerang integration can be run in standalone mode and does not need to be connected to an EIB.
3. **c - Custom Report** and **c - Custom Transformation** (no need for an EIB with this option) - This option allows you to run the Boomerang as a standalone integration. An EIB is not needed because Boomerang will extract your report data and perform the tranformation. Select a Workday report, then attach the XSLT document to be used to transform the report output. Boomerang does not support reports with prompts at this time.

![image](https://user-images.githubusercontent.com/413552/124685009-44ba2a80-de85-11eb-9632-e48dec777cf7.png)

## EIB Instructions

1. Develop a Workday Custom Report. The report will be used to extract data for a web service request (see the sample report and sample xml output below).
2. Create an Outbound EIB.
3. Select the Workday report from step one as your data source.
4. In the EIB, use the transformation step to convert the output into a web service request (see the sample xslt, 'department_assignment_automation.xslt', below).
5. Add a business process to the EIB.
6. Insert a new step at the end of the business process to call the `Boomerang` integration.
7. For the `Boomerang` integration configuration, update the Web Service launch parameter to make sure that it matches the request in your XSLT file. 
8. You are not required to configure any additional Boomerang launch parameters for this option.

### (Step 5 Above) Add a business process to the EIB and insert a new step
![image](https://user-images.githubusercontent.com/413552/125008154-c2fa0680-e016-11eb-8551-dda6e78c8298.png)

### (Step 6 Above) Select "Integration" and choose the Boomerang integration
![image](https://user-images.githubusercontent.com/413552/125008820-2c2e4980-e018-11eb-9dc9-5571b1126a2b.png)

### (Step 7 Above) Configure the Boomerang integration

![image](https://user-images.githubusercontent.com/413552/129071201-40fa7c4d-2f01-4262-9ee5-0ff1819b6b59.png)


### Sample Custom Report

<img src="https://user-images.githubusercontent.com/413552/129069245-71a4e3e3-d7ab-4587-9b0b-dda126b956de.png" width="600" />

### Sample XML Report Output

```xml
<?xml version='1.0' encoding='UTF-8'?>
<wd:Report_Data xmlns:wd="urn:com.workday.report/Department_Assignment_Automation">
	<wd:Report_Entry>
		<wd:Position_ID>Job-7-12345</wd:Position_ID>
		<wd:Reference_ID>DEPT-143728</wd:Reference_ID>
	</wd:Report_Entry>
</wd:Report_Data>
```

### Sample XSLT Transformation
#### department_assignment_automation.xslt
```xml
<?xml version='1.0' encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wd="urn:com.workday.report/Department_Assignment_Automation">
  <xsl:template match="/">
    <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
      <env:Body>
        <xsl:for-each select="wd:Report_Data/wd:Report_Entry">
          <Change_Organization_Assignments_Request xmlns="urn:com.workday/bsvc" xmlns:a="urn:com.workday/bsvc" a:version="v27.2">
            <a:Change_Organization_Assignments_Data a:Effective_Date="{format-date(current-date(), '[Y0001]-[M01]-[D01]')}">
              <a:Position_Reference>
                <a:ID a:type="Position_ID">
                  <xsl:value-of select="wd:Position_ID" />
                </a:ID>
              </a:Position_Reference>
              <a:Position_Organization_Assignments_Data>
                <a:Custom_Organization_Assignment_Data>
                  <a:Custom_Organization_Assignment_Reference>
                    <a:ID a:type="Custom_Organization_Reference_ID">
                      <xsl:value-of select="wd:Reference_ID" />
                    </a:ID>
                  </a:Custom_Organization_Assignment_Reference>
                </a:Custom_Organization_Assignment_Data>
              </a:Position_Organization_Assignments_Data>
            </a:Change_Organization_Assignments_Data>
          </Change_Organization_Assignments_Request>
        </xsl:for-each>
      </env:Body>
    </env:Envelope>
  </xsl:template>
</xsl:stylesheet>
```


Boomerang is not sponsored, affiliated with, or endorsed by Workday®.
