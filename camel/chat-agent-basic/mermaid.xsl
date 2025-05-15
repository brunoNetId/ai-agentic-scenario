<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:custom="http://myfunctions" version="3.0">
    <xsl:output method="text" indent="yes"/>
    <xsl:decimal-format name="euro" decimal-separator="," grouping-separator="."/>

    <!-- Define the custom function -->
    <xsl:function name="custom:number-to-letter">
        <xsl:param name="number"/>
        <!-- Convert number to string and use translate -->
        <xsl:value-of select="translate(string($number), '012345678910111213141516171819202122232425', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:function>

    <xsl:template match="/">
---
config:
    flowchart:
        subGraphTitleMargin:
            bottom: 40
---
graph LR
        <xsl:for-each select="/ArrayList/item">
            <xsl:variable name="task" select="custom:number-to-letter(./step+1)"/>
<xsl:text>&#10;</xsl:text>
<xsl:value-of select="custom:number-to-letter(./step)"/><xsl:value-of select="if (position()=1) then '[Start]' else ''"/> -- Step <xsl:value-of select="./step"/> --&gt;  <xsl:value-of select="$task"/>
<xsl:text>&#10;</xsl:text>
subgraph <xsl:value-of select="$task"/>[<xsl:value-of select="execution/description"/>]
    direction TB
                <xsl:for-each select=".//tool_calls">
                    <xsl:choose>
                        <xsl:when test="count(group) = 1">
                            <xsl:variable name="callnumber" select="position()"/>
                            <xsl:for-each select="group">
    subgraph <xsl:value-of select="concat($task,'S', $callnumber)"/>[Group calls <xsl:value-of select="$callnumber"/>]
        direction TB
        <xsl:value-of select="concat($task,'S', $callnumber,'-start')"/>[<xsl:value-of select=".//name"/>] --&gt; <xsl:value-of select="concat($task,'S', $callnumber,'-end')"/>[<xsl:value-of select=".//response"/>]
    end
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="callnumber" select="position()"/>
                            <xsl:variable name="group" select="concat($task,'S', $callnumber)"/>
    subgraph <xsl:value-of select="$group"/>[Group calls <xsl:value-of select="$callnumber"/>]
        direction TB
                                <xsl:for-each select="group">
                                    <xsl:variable name="groupnumber" select="position()"/>
        <xsl:text>&#10;</xsl:text>
        <xsl:value-of select="concat($group,'-',$groupnumber,'-start')"/>[<xsl:value-of select=".//name"/>] --&gt; <xsl:value-of select="concat($group,'-',$groupnumber,'-end')"/>[<xsl:value-of select=".//response"/>]
                                </xsl:for-each>
    end
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
                <xsl:for-each select=".//tool_calls">
                    <xsl:variable name="callnumber" select="position()"/>
                    <xsl:choose>
                        <xsl:when test="$callnumber > 1">
    <xsl:text>&#10;</xsl:text>
    <xsl:value-of select="concat($task,'S', $callnumber - 1)"/> --&gt; <xsl:value-of select="concat($task,'S', $callnumber)"/>
                        </xsl:when>
                    </xsl:choose>                
                </xsl:for-each>
end
        </xsl:for-each>
<xsl:text>&#10;</xsl:text>
<xsl:value-of select="custom:number-to-letter(count(/ArrayList/item))"/> --&gt; <xsl:text>End</xsl:text>       
    </xsl:template>
</xsl:stylesheet>