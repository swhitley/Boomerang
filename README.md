# Boomerang

A Workday boomerang integration uses data from Workday to generate a Workday update.  `Boomerang` is a Workday Studio integration that can be run without any code changes. Simply deploy Boomerang, and launch a boomerang integration with a report and an XSLT file.

- Boomerang can be run as part of an EIB, or it can be launched as a standalone integration. 
- Messages are logged using multiple output options, including Cloud Logs.

Use launch option `c` (below) as the quickest way to get started with Boomerang. 
1. Select the report that will serve as the boomerang input, **c - Custom Report**.  
2. Attach the XSLT document using **c - Custom Transformation**.

## Installation
1. Download the latest clar file from https://github.com/swhitley/Boomerang/releases/latest.
2. Import the clar file into Workday Studio.
3. Deploy the integration to your Workday tenant.

## Launch Options

There are three separate options for launching Boomerang. When selecting an option (a, b, or c), you must leave the parameters for the other options blank.

1. **a - Event Document Name Contains** - This is the direct replacement for the legacy `WebServiceRequester` integration. The parameter may be left blank when being used with an EIB, and the integration will pick up the deliverable document from the EIB.  If there are multiple deliverable documents, use this parameter to match and select the desired deliverable document for processing.
2. **b - Input Document** - A fully formed SOAP request document can be attached at runtime. The Boomerang integration can be run in standalone mode and does not need to be connected to an EIB.
3. **c - Custom Report** and **c - Custom Transformation** - Run the Boomerang as a standalone integration.  Select a Workday report, then attach the XSLT document to be used to transform the report output. Boomerang does not support reports with prompts at this time.

![image](https://user-images.githubusercontent.com/413552/124685009-44ba2a80-de85-11eb-9632-e48dec777cf7.png)

## EIB Instructions

1. Create an Outbound EIB.
2. In the EIB, use the transformation step to convert the output into a SOAP call (see the sample xslt, 'department_assignment_automation.xslt', below).
3. Add a business process to the EIB.
4. Insert a new step at the end of the business process to call the `Boomerang` integration. 
5. You do not need to configure any of the Boomerang launch parameters for this option.

![image](https://user-images.githubusercontent.com/413552/125008154-c2fa0680-e016-11eb-8551-dda6e78c8298.png)
![image](https://user-images.githubusercontent.com/413552/125008820-2c2e4980-e018-11eb-9dc9-5571b1126a2b.png)


### department_assignment_automation.xslt
```xml
<?xml version='1.0' encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wd="urn:com.workday.report/Department_Assignment_Automation">
  <xsl:template match="/">
    <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
      <env:Body>
        <xsl:for-each select="wd:Report_Data/wd:Report_Entry">```
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
