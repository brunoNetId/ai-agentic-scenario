<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xml" indent="yes"/>
    <xsl:decimal-format name="euro" decimal-separator="," grouping-separator="."/>

    <xsl:template match="/">

        <div style="position: relative; float: right; margin-top: 20px; margin-right: 20px; background-color: rgba(255, 0, 0, 0.7); color: white; padding: 5px 20px; font-weight: bold; transform: rotate(15deg); font-size: 14px; box-shadow: 0 0 10px rgba(0,0,0,0.3);">Amended</div>


<div style="display: flex; justify-content: flex-start; align-items: flex-start; width: 100%; max-width: 1200px; margin: 0 auto; gap: 0px;">
    <div style="flex: 0 0 100%;">
      <h3>Invoice no: <xsl:value-of select="/Invoice/InvoiceNumber"/></h3>
      <span style="margin-right: 200px;">Date of issue:</span>

<!--       <div>
      <span >Date of issue:</span>
      </div>
      <div>
            <span style="margin-right: 100px; color: white;">Date of issue:</span>
        </div> -->
<!-- 
    <div style="display: flex; ">
      <div >
        <span >Date of issue:</span>
      </div>
      <div >
        <span style="margin-right: 110px; color: white;">Date of issue:</span>
      </div> -->

<!-- 
      <div style="position: relative;">

      <span style="z-index: 2; margin-right: 200px;">Date of issue:</span>
      <span style="z-index: 1; margin-right: 200px;">Date of issue:</span>
      </div> -->
<!-- 
<div style="display: grid; grid-template-areas: 'stack';">
    <span style="grid-area: stack; z-index: 2; color: red;">Text on Top</span>
    <span style="grid-area: stack; z-index: 1; color: blue;">Text Below</span>
  </div> -->

      <!-- <div style="position: relative;"> -->
<!--         <span style="position: absolute; top: 0; left: 0; z-index: 2; margin-right: 200px;">Date of issue:</span>
        <span style="position: absolute; top: 0; left: 0; z-index: 1; margin-right: 200px;">Date of issue:</span> -->
      <!-- </div> -->

      <span>                
            <xsl:variable name="dateofissue" select="/Invoice/DateOfIssue"/>
            <xsl:value-of select="concat(substring($dateofissue, 1, 2), '/', substring($dateofissue, 3, 2), '/', substring($dateofissue, 5, 4))"/>
      </span>
      <br/>
      <!-- <span style="color: white;">Dummy Line necessary for PDF to JSON Docling operation.</span> -->

    <!-- </div> -->

    </div>
</div>

<br/>
<br/>
<br/>
<br/>

<div style="flex: 0 0 30px; display: flex; justify-content: flex-start; align-items: flex-start; width: 100%; max-width: 1200px; margin: 0 auto; gap: 0px;">
    <div style="flex: 0 0 300px;">

      <h3>Seller:</h3>
      <xsl:value-of select="/Invoice/Seller/Name"/><br/>
      <xsl:value-of select="/Invoice/Seller/Address/Street"/><br/>
      <xsl:value-of select="/Invoice/Seller/Address/City"/>, <xsl:value-of select="concat(/Invoice/Seller/Address/State,' ',/Invoice/Seller/Address/PostalCode)"/><br/>
      <br/>
      <xsl:variable name="tax" select="/Invoice/Seller/TaxId"/>
      Tax Id: <xsl:value-of select="concat(substring($tax,1,3), '-', substring($tax,4,2), '-', substring($tax,6,4))"/><br/>
      IBAN: <xsl:value-of select="/Invoice/Seller/IBAN"/><br/>
    </div>
    <div style="flex: 0 0 300px; display: flex; justify-content: left;">
      <div>
        <h3>Client:</h3>
      <xsl:value-of select="/Invoice/Client/Name"/><br/>
      <xsl:value-of select="/Invoice/Client/Address/Street"/><br/>
      <xsl:value-of select="/Invoice/Client/Address/City"/>, <xsl:value-of select="concat(/Invoice/Client/Address/State,' ',/Invoice/Client/Address/PostalCode)"/><br/>
      <br/>
      <xsl:variable name="tax" select="/Invoice/Client/TaxId"/>
      Tax Id: <xsl:value-of select="concat(substring($tax,1,3), '-', substring($tax,4,2), '-', substring($tax,6,4))"/><br/>

      </div>
    </div>
</div>


<h3>ITEMS</h3>
<table>
    <thead>
        <tr>
            <th>No.</th>
            <th>Description</th>
            <th style="text-align:right;">Qty</th>
            <th style="text-align:right;">UM</th>
            <th style="width:12%; text-align:right;">Net price</th>
            <th style="width:13%; text-align:right;">Net worth</th>
            <th style="width:12%; text-align:right;">VAT [%]</th>
            <th style="text-align:right;">Gross worth</th>
        </tr>
    </thead>
    <tbody>
        <xsl:for-each select="/Invoice/Items/Item">
        <!-- <xsl:for-each select="/Invoice/Items"> -->
            <tr>
                <td><xsl:value-of select="Number"/>.</td>
                <td><xsl:value-of select="Description"/></td>
                <td style="text-align:right;"><xsl:value-of select="format-number(Quantity,  '#.###,00', 'euro')"/></td>
                <td style="text-align:right;"><xsl:value-of select="lower-case(UnitOfMeasure)"/></td>
                <td style="text-align:right;"><xsl:value-of select="format-number(NetPrice,  '#.###,00', 'euro')"/></td>
                <td style="text-align:right;"><xsl:value-of select="format-number(NetWorth,  '#.###,00', 'euro')"/></td>
                <td style="text-align:right;"><xsl:value-of select="VATPercentage"/>%</td>
                <td style="text-align:right;"><xsl:value-of select="format-number(GrossWorth,'#.###,00', 'euro')"/></td>
            </tr>
        </xsl:for-each>
    </tbody>
</table>

<h3>SUMMARY</h3>
<table>
    <thead>
        <tr>
            <th style="width:30%;"></th>
            <th style="text-align:right;">VAT [%]</th>
            <th style="text-align:right;">Net worth</th>
            <th style="text-align:right;">VAT</th>
            <th style="text-align:right;">Gross worth</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td></td>
            <td style="text-align:right;"><xsl:value-of select="/Invoice/Summary/VATPercentage"/>%</td>
            <td style="text-align:right;"><xsl:value-of select="format-number(/Invoice/Summary/NetWorth,  '#.###,00', 'euro')"/></td>
            <td style="text-align:right;"><xsl:value-of select="format-number(/Invoice/Summary/VATAmount,'#.###,00', 'euro')"/></td>
            <td style="text-align:right;"><xsl:value-of select="format-number(/Invoice/Summary/GrossWorth,'#.###,00', 'euro')"/></td>
        </tr>
        <tr style="text-align:right; font-weight:bold">
            <td >Total</td>
            <td></td>
            <td>$ <xsl:value-of select="format-number(/Invoice/Summary/NetWorth,  '#.###,00', 'euro')"/></td>
            <td>$ <xsl:value-of select="format-number(/Invoice/Summary/VATAmount,'#.###,00', 'euro')"/></td>
            <td>$ <xsl:value-of select="format-number(/Invoice/Summary/GrossWorth,'#.###,00', 'euro')"/></td>
        </tr>
    </tbody>
</table>



    </xsl:template>
</xsl:stylesheet>