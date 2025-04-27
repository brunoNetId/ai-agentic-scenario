<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <Invoice>
            <InvoiceNumber>
                <xsl:value-of select="/LinkedHashMap/InvoiceNumber"/>
            </InvoiceNumber>
            <DateOfIssue>
                <xsl:value-of select="/LinkedHashMap/DateOfIssue"/>
            </DateOfIssue>
            <Seller>
                <Name>
                    <xsl:value-of select="replace(/LinkedHashMap/Seller[1],'^(.*?)\s+\d+\S*.*$', '$1')"/>
                </Name>
                <Address>
                    <Street>
                        <xsl:value-of select="replace(/LinkedHashMap/Seller[1], '^[^0-9]*(\d+\S*(?:\s+\S+)*?)\s+\w+,\s+\w{2}\s+\d+$', '$1')"/>
                    </Street>
                    <City>
                        <xsl:value-of select="replace(/LinkedHashMap/Seller[1], '.*\s+(\w+),\s+\w{2}\s+\d+$', '$1')"/>
                    </City>
                    <State>
                        <xsl:value-of select="replace(/LinkedHashMap/Seller[1], '.*,\s+(\w{2})\s+\d+$', '$1')"/>
                    </State>
                    <PostalCode>
                        <xsl:value-of select="replace(/LinkedHashMap/Seller[1], '.*\s+(\d+)$', '$1')"/>
                    </PostalCode>
                </Address>
                <TaxId>
                    <xsl:value-of select="replace(substring-after(/LinkedHashMap/Seller[2],': '),'-','')"/>
                </TaxId>
                <IBAN>
                    <xsl:value-of select="substring-after(/LinkedHashMap/Seller[3],': ')"/>
                </IBAN>
            </Seller>
            <Client>

                <Name>
                    <xsl:value-of select="replace(/LinkedHashMap/Client[1],'^(.*?)\s+\d+\S*.*$', '$1')"/>
                </Name>
                <Address>
                    <Street>
                        <xsl:value-of select="replace(/LinkedHashMap/Client[1], '^[^0-9]*(\d+\S*(?:\s+\S+)*?)\s+\w+,\s+\w{2}\s+\d+$', '$1')"/>
                    </Street>
                    <City>
                        <xsl:value-of select="replace(/LinkedHashMap/Client[1], '.*\s+(\w+),\s+\w{2}\s+\d+$', '$1')"/>
                    </City>
                    <State>
                        <xsl:value-of select="replace(/LinkedHashMap/Client[1], '.*,\s+(\w{2})\s+\d+$', '$1')"/>
                    </State>
                    <PostalCode>
                        <xsl:value-of select="replace(/LinkedHashMap/Client[1], '.*\s+(\d+)$', '$1')"/>
                    </PostalCode>
                </Address>
                <TaxId>
                    <xsl:value-of select="replace(substring-after(/LinkedHashMap/Client[2],': '),'-','')"/>
                </TaxId>
            </Client>
            <Items>
                <xsl:for-each select="/LinkedHashMap/Items">
                    <Item>
                        <Number>
                            <xsl:value-of select="replace(Item[1],'\.','')"/>
                        </Number>
                        <Description>
                            <xsl:value-of select="Item[2]"/>
                        </Description>
                        <Quantity>
                            <xsl:value-of select="replace(Item[3],',','.')"/>
                        </Quantity>
                        <UnitOfMeasure>
                            <xsl:value-of select="Item[4]"/>
                        </UnitOfMeasure>
                        <NetPrice>
                            <xsl:value-of select="replace(Item[5],',','.')"/>
                        </NetPrice>
                        <NetWorth>
                            <xsl:value-of select="replace(Item[6],',','.')"/>
                        </NetWorth>
                        <VATPercentage>
                            <xsl:value-of select="replace(Item[7],'%','')"/>
                        </VATPercentage>
                        <GrossWorth>
                            <xsl:value-of select="replace(Item[8],',','.')"/>
                        </GrossWorth>
                    </Item>
                </xsl:for-each>
            </Items>
            <Summary>
                <VATPercentage>
                    <xsl:value-of select="replace(/LinkedHashMap/Summary[1]/Entry[1],'%','')"/>
                </VATPercentage>
                <NetWorth>
                    <xsl:value-of select="replace(/LinkedHashMap/Summary[1]/Entry[2],',','.')"/>
                </NetWorth>
                <VATAmount>
                    <xsl:value-of select="replace(/LinkedHashMap/Summary[1]/Entry[3],',','.')"/>
                </VATAmount>
                <GrossWorth>
                    <xsl:value-of select="replace(/LinkedHashMap/Summary[1]/Entry[4],',','.')"/>
                </GrossWorth>
            </Summary>
        </Invoice>
    </xsl:template>
</xsl:stylesheet>