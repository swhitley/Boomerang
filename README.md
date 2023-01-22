# Boomerang-v2

A boomerang integration exports data out of Workday, transforms the data, and then feeds the data back to Workday to perform an update (like a boomerang <img src="https://user-images.githubusercontent.com/413552/129063819-869e47d6-b847-4f59-9f14-76ddb7539880.png" width="16" />
).

A Workday boomerang integration has three components:
1. A Workday report (RaaS) to extract data from Workday.
2. An XSLT file to transform the data into a web service request.
3. A Workday Studio application that takes the web service request and calls Workday's API to perform a data update.

This application, `Boomerang-v2`, is a Workday Studio integration that can be run without any code changes. Simply deploy Boomerang and launch a boomerang integration with a report and an XSLT file.

- Boomerang can be run as part of an EIB, or it can be launched as a standalone integration.
- With the *template* option, Boomerang is truly a no-code solution (no Studio code and no XSLT).  Boomerang will convert the request template to XSLT and then transform the report document.
- Messages are logged using multiple output options, including Cloud Logs.

## Installation
1. Download the latest clar file from https://github.com/swhitley/Boomerang/releases/latest.
2. Import the clar file into Workday Studio.
3. Deploy the integration to your Workday tenant.

## Quick Start

An EIB is often used as input to `Boomerang`, but it is also possible to run Boomerang as a standalone app. 

1. Launch the `Boomerang-v2`integration.
2. Select the `Web Service` that corresponds to the web service request in your XSLT.
3. Select the XSLT document from Workday Drive using **Custom XSLT or Template (opt)**.
4. Select the report that will serve as the boomerang input in **Custom Report (opt)**.

The report input will be transformed by the XSLT and the web service request will be executed in Workday.

**Reminder:**  The namespace in your XSLT (example: the text following `xmlns:wd=`) must match the namespace on your Workday report (under `Web Service Options`).

**Tip:** If you always override your Workday report's namespace with `urn:com.workday/bsvc` (instead of the Workday-generated namespace), you'll be less likely to have a mismatch in your XSLT.

## EIB Instructions (most common approach)

1. Develop a Workday Custom Report. The report will be used to extract data for a web service request (see the sample report and sample xml output below).
2. Create an Outbound EIB.
3. Select the Workday report from step one as your data source.
4. In the EIB, use the transformation step to convert the output into a web service request (see the sample xslt, 'department_assignment_automation.xslt', below).
5. Add a business process to the EIB.
6. Insert a new step at the end of the business process to call the `Boomerang-v2` integration.
7. For the Boomerang integration parameters, update the Web Service launch parameter so it matches the request in your XSLT file. 
8. You are not required to configure any additional Boomerang launch parameters for this option.

### (Step 5 Above) Add a business process to the EIB and insert a new step
![image](https://user-images.githubusercontent.com/413552/125008154-c2fa0680-e016-11eb-8551-dda6e78c8298.png)

### (Step 6 Above) Select "Integration" and choose the Boomerang-v2 integration
<img width="320" alt="image" src="https://user-images.githubusercontent.com/413552/213903737-86b79a14-c6c4-4675-890f-13ea6edc5a40.png">

### (Step 7 Above) Configure the Boomerang integration

<img width="668" alt="image" src="https://user-images.githubusercontent.com/413552/213896594-71ce75e4-6846-4b10-a1a1-3e8b8b089e35.png">


## Launch Parameters

For the EIB solution, you may only need to set the `Web Service` and `Web Service API Version` parameters. Leave all other parameters blank if the report was already transformed in an EIB.

1. **Web Service** - This is the Workday Web Service that matches your SOAP request (operation).  See the [Workday Web Services Directory](https://community.workday.com/sites/default/files/file-hosting/productionapi/index.html).
2. **Web Service API Version** - The Workday Web Service version that matches your request.
3. **Custom XSLT or Template (opt)** - A Workday Drive document that can be used to transform report input if the transformation did not occur in an EIB (see **Custom Transformation** for more information).
4. **Event Doc Name Contains (opt)** - The parameter may be left blank. If there are multiple deliverable documents from an EIB, use this parameter to match the name of the desired deliverable document.- 
6. **Custom Report (opt)** - This parameter is not needed if the report input is coming from an EIB.  To use this parameter, select a Workday report. The **Custom XSLT or Template (opt)** parameter must be used in conjunction with this parameter. Boomerang does not support reports with prompts at this time.
7. **Validate Only** - When this box is checked, the integration will run, but the SOAP API call to Workday will be performed in valide-only mode.

<img width="668" alt="image" src="https://user-images.githubusercontent.com/413552/213896594-71ce75e4-6846-4b10-a1a1-3e8b8b089e35.png">

### Custom Transformation

If the report input has not been transformed in an EIB, the xslt document in the **Custom XSLT or Template (opt)** parameter can be used to convert the XML into a SOAP request.  This parameter is useful for boomerang chains. To create a boomerang chain, select 'None' for the transformation in the EIB.  Create multiple integration steps in the EIB business process and attach different XSLT documents in this parameter to generate different transformations.  Boomerang chains are useful when generating different requests using the same report input.  For example, it is possible to create new supervisory orgs in one integration step, and in the next integration step, assign the manager roles.  The trick is to use different xslt documents for each boomerang step, which can be done with this parameter.

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
