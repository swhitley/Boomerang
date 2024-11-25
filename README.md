# Boomerang

A boomerang integration exports data out of Workday, transforms the data, and then feeds the data back to Workday to perform an update (like a boomerang <img src="https://user-images.githubusercontent.com/413552/129063819-869e47d6-b847-4f59-9f14-76ddb7539880.png" width="16" />
).

A classic Workday boomerang integration has three components:
1. A Workday report (RaaS) to extract data from Workday.
2. An XSLT file to transform the data into a web service request.
3. A Workday Studio or Orchestrate for Integrations application that takes the web service request and calls Workday's API to perform a data update.

There are two applications in this project, a Workday Studio applcation called, `Boomerang-v2`, and an Orchestrate application called, `Boomerange (O4I)`.  Both applications can run without any code changes. Simply deploy Boomerang and launch a boomerang integration with a report and an XSLT file.

A few of the advanced features are only available in the Studio version.

| Supported Parameters              | Workday Studio | O4I |
|-----------------------------------|:--------------:|:---:|
| **Web Service**                   |        X       |  X  |
| **Web Service API Version**       |        X       |  X  |
| **Custom XSLT or Template (opt)** |        X       |     |
| **Event Doc Name Contains (opt)** |        X       |     |
| **Custom Report (opt)**           |        X       |     |
| **Validate Only**                 |        X       |  X  |


For the Studio version:
- Boomerang can be run as part of an EIB, or it can be launched as a standalone integration.
- With the *template* option, no coding is required (no Studio code and no XSLT).  Boomerang will convert the request template to XSLT and then transform the report document.
- Messages are logged using multiple output options, including Cloud Logs.

## Workday Studio Installation
1. Download the latest clar file from https://github.com/swhitley/Boomerang/releases/latest.
2. Import the clar file into Workday Studio.
3. Deploy the integration to your Workday tenant.

## Orchestrate Installation
1. Download the latest Boomerang (O4I) zip file from https://github.com/swhitley/Boomerang/releases/latest.
2. Signon to https://developer.workday.com/ and import the file into a new Orchestrate for Integrations application.
3. Promote the integration to your Workday tenant.
4. Create an Integration System using the Orchestrate for Integration template.
5. Connect the integration system to the Orchestrate application by setting the Application Reference ID.
6. Create the launch parameters as documented in Boomerang_(O4I).xlsx.

## EIB Instructions (most common approach for Studio and Orchestrate)

1. Develop a Workday Custom Report. The report will be used to extract data for a web service request (see the sample report and sample xml output below).
2. Create an Outbound EIB.
3. Select the Workday report from step one as your data source.
4. In the EIB, use the transformation step to convert the output into a web service request (see the sample xslt below).
5. Add a business process to the EIB.
6. Insert a new step at the end of the business process to call the `Boomerang-v2` or `Boomerang (O4I)` integration.
7. For the Boomerang integration parameters, update the Web Service launch parameter so it matches the request in your XSLT file. 
8. You are not required to configure any additional Boomerang launch parameters for this option.

**Reminder:**  The namespace in your XSLT (example: the text following `xmlns:wd=`) must match the namespace on your Workday report (under `Web Service Options`).

**Tip:** If you always override your Workday report's namespace with `urn:com.workday/bsvc` (instead of the Workday-generated namespace), you'll be less likely to have a mismatch in your XSLT.

### (Step 5 Above) Add a business process to the EIB and insert a new step
![image](https://user-images.githubusercontent.com/413552/125008154-c2fa0680-e016-11eb-8551-dda6e78c8298.png)

### (Step 6 Above) Select "Integration" and choose Boomerang-v2 or Boomerang (O4I)
<img width="320" alt="image" src="https://user-images.githubusercontent.com/413552/213903737-86b79a14-c6c4-4675-890f-13ea6edc5a40.png">

### (Step 7 Above) Configure the Boomerang integration

<img width="668" alt="image" src="https://user-images.githubusercontent.com/413552/213896594-71ce75e4-6846-4b10-a1a1-3e8b8b089e35.png">

## Quick Start (Studio version)

Although an EIB is often used as input, Boomerang can also be run as a standalone app. If a RaaS has already been created, and the XSLT document is stored in Workday Drive, follow the instructions below to quickly launch a boomerang integration. 

1. Launch the `Boomerang-v2`integration.
2. Select the `Web Service` that corresponds to the web service request in your XSLT.
3. Select the XSLT document from Workday Drive using **Custom XSLT or Template (opt)**.
4. Select the report that will serve as the boomerang input in **Custom Report (opt)**.

The report input will be transformed by the XSLT and the web service request will be executed in Workday.

## Launch Parameters

For the EIB solution, you may only need to set the `Web Service` and `Web Service API Version` parameters. Leave all other parameters blank if the report was already transformed in an EIB.

1. **Web Service** - This is the Workday Web Service that matches your SOAP request (operation).  See the [Workday Web Services Directory](https://community.workday.com/sites/default/files/file-hosting/productionapi/index.html).
2. **Web Service API Version** - The Workday Web Service version that matches your request.
3. **Custom XSLT or Template (opt)** - The parameter may be left blank. The parameter points to a Workday Drive document that can be used to transform report input if the transformation did not occur in an EIB (see **Custom Transformation** and **Workday Drive** for more information).
4. **Event Doc Name Contains (opt)** - The parameter may be left blank. If there are multiple deliverable documents from an EIB, use this parameter to match the name of the desired deliverable document.
5. **Custom Report (opt)** - This parameter is not needed if the report input is coming from an EIB.  To use this parameter, select a Workday report. The **Custom XSLT or Template (opt)** parameter must be used in conjunction with this parameter. Boomerang does not support reports with prompts at this time.
6. **Validate Only** - When this box is checked, the integration will run, but the SOAP API call to Workday will be performed in validate-only mode.

<img width="668" alt="image" src="https://user-images.githubusercontent.com/413552/213896594-71ce75e4-6846-4b10-a1a1-3e8b8b089e35.png">

### Custom Transformation (Studio only)

If the report input has not been transformed in an EIB, the xslt document in the **Custom XSLT or Template (opt)** parameter can be used to convert the XML into a SOAP request.  This parameter is useful for boomerang chains. To create a boomerang chain, select 'None' for the transformation in the EIB.  Create multiple integration steps in the EIB business process and attach different XSLT documents in this parameter to generate different transformations.  Boomerang chains are useful when generating different requests using the same report input.  For example, it is possible to create new supervisory orgs in one integration step, and in the next integration step, assign the manager roles.  The trick is to use different xslt documents for each boomerang step, which can be done with this parameter.

### Workday Drive (Studio only)

Boomerang accesses Workday Drive when the **Custom XSLT or Template (opt)** parameter is used. Some configuration of Workday Drive and knowledge of files in Drive are helpful when using this parameter.

For this parameter, ensure the following domain is enabled for the Workday user that is executing Boomerang-v2

Security:  `Domain: Drive Web Services`

Workday Drive will only allow certain file types (based on file extension) to be uploaded (see the Workday Drive documentation).  It is possible to upload an XSLT file or template file to Drive by changing the extension of the file.  For example, if the XSLT file is called "Change_Business_Title.xslt," Workday Drive may block the upload of this file extension.  In this case, change the extension to a file type that is accepted by Drive (such as .svg or .png).  Using the example, the file can be uploaded as, "Change_Business_Title.xslt.png."  Even though the file extension doesn't match the file contents, Boomerang will still recognize the file and use it in a transformation.

When using the **Custom XSLT or Template (opt)** parameter, it is best to navigate in the selection field using the **By Type** option and then by **File**. Workday Drive carries a reference to the file and a view of the file as separate objects.  It is important to select the file object, and not the view object.

### Request Template (Studio only)

With the Template parameter, XSLT coding is not required to execute a Boomerang integration.

A template is an XML document that contains references to the fields in the input report.

Follow these steps to setup a valid template:

1. Ensure that the RaaS that is used for the boomerang is using the standard namespace `urn:com.workday/bsvc`.  This value is set when clicking the **Enable as Web Service** flag. Override the default value and set the namespace to `urn:com.workday/bsvc`. 
2. Construct a valid API request document using the information available on the [Workday Web Services Directory](https://community.workday.com/sites/default/files/file-hosting/productionapi/index.html). Remember that you are not constructing an XML stylesheet.  This is a basic XML document.
3. Wherever dynamic data is needed in your request document, add the field name from your report input, surrounded by double-braces.  For example, if your input report contains a field called "costCenter", add the following text to your template where the cost center should appear:  `{{costCenter}}`
4. When Boomerang runs, it will convert your template into an XSL transformation.  The double-braces will be replaced by xsl:value-of code.  The field name will be prefixed with `wd:`.
5. To prevent a `wd:` prefix from being applied automatically, use a tilde (~) immediately following the double-braces.  See the example template where an XSLT function is used to get the current date.


See the sample below for a request template that can be used to update a business title.



## Sample Files

### Sample Custom Report

<img width="503" alt="image" src="https://user-images.githubusercontent.com/413552/213957384-1b6c66eb-90de-4fca-83db-83eff118baf5.png">

### Sample XML Report Output

```xml
<?xml version='1.0' encoding='UTF-8'?>
<wd:Report_Data xmlns:wd="urn:com.workday/bsvc">
	<wd:Report_Entry>
		<wd:Employee_ID>006447</wd:Employee_ID>
		<wd:Title>Master Boomerang Thrower</wd:Title>
	</wd:Report_Entry>
</wd:Report_Data>
```

### Sample XSLT File

**NOTE:**  The Change Business Title Request web operation is part of the Human Resources service.  If you use this example, you will select Human Resources in Boomerang's Web Service parameter.

#### change_business_title.xslt
```xml
<?xml version='1.0' encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wd="urn:com.workday/bsvc">
	<xsl:template match="/">
		<env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			<env:Body>
				<xsl:for-each select="wd:Report_Data/wd:Report_Entry">
					<bsvc:Change_Business_Title_Request bsvc:version="v39.1" xmlns:bsvc="urn:com.workday/bsvc">
						<bsvc:Business_Process_Parameters>
							<bsvc:Auto_Complete>true</bsvc:Auto_Complete>
							<bsvc:Run_Now>true</bsvc:Run_Now>
							<bsvc:Discard_On_Exit_Validation_Error>true</bsvc:Discard_On_Exit_Validation_Error>
						</bsvc:Business_Process_Parameters>
						<bsvc:Change_Business_Title_Business_Process_Data>
							<bsvc:Worker_Reference>
								<bsvc:ID bsvc:type="Employee_ID"><xsl:value-of select="wd:Employee_ID"/></bsvc:ID>
							</bsvc:Worker_Reference>
							<bsvc:Change_Business_Title_Data>
								<bsvc:Event_Effective_Date>
									<xsl:value-of select="format-date(current-date(), '[Y0001]-[M01]-[D01]')"/>
								</bsvc:Event_Effective_Date>
								<bsvc:Proposed_Business_Title><xsl:value-of select="wd:Title"/></bsvc:Proposed_Business_Title>
							</bsvc:Change_Business_Title_Data>
						</bsvc:Change_Business_Title_Business_Process_Data>
					</bsvc:Change_Business_Title_Request>
				</xsl:for-each>
			</env:Body>
		</env:Envelope>
	</xsl:template>
</xsl:stylesheet>
```

### Sample Request Template
#### change_business_title.template.xml
```xml
<?xml version='1.0' encoding="UTF-8"?>
<bsvc:Change_Business_Title_Request xmlns:bsvc="urn:com.workday/bsvc" bsvc:version="v39.1">
	<bsvc:Business_Process_Parameters>
		<bsvc:Auto_Complete>true</bsvc:Auto_Complete>
		<bsvc:Run_Now>true</bsvc:Run_Now>
		<bsvc:Discard_On_Exit_Validation_Error>true</bsvc:Discard_On_Exit_Validation_Error>
	</bsvc:Business_Process_Parameters>
	<bsvc:Change_Business_Title_Business_Process_Data>
		<bsvc:Worker_Reference>
			<bsvc:ID bsvc:type="Employee_ID">{{Employee_ID}}</bsvc:ID>
		</bsvc:Worker_Reference>
		<bsvc:Change_Business_Title_Data>
			<bsvc:Event_Effective_Date>{{~format-date(current-date(), '[Y0001]-[M01]-[D01]')}}</bsvc:Event_Effective_Date>
			<bsvc:Proposed_Business_Title>{{Title}}</bsvc:Proposed_Business_Title>
		</bsvc:Change_Business_Title_Data>
	</bsvc:Change_Business_Title_Business_Process_Data>
</bsvc:Change_Business_Title_Request>
```

## Additional Information
**Boomerang is not sponsored, affiliated with, or endorsed by Workday®.**
