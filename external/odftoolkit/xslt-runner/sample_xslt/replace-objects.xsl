<?xml version="1.0" encoding="UTF-8" ?>
<!--

  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER

  Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.

  Use is subject to license terms.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
  obtain a copy of the License at http://odftoolkit.org/docs/license.txt

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

  See the License for the specific language governing permissions and
  limitations under the License.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xhtml xsl"
                version="1.0">
    <xsl:output method="xml" indent="no"/>

    <xsl:param name="ref-html"/>

    <xsl:template match="draw:frame[draw:object]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:variable name="new-src" select="concat('Pictures/',document($ref-html)//img[@name=current()/@draw:name]/@src)"/>
            <draw:image xlink:href="{$new-src}" xlink:type="simple" xlink:show="embed" xlink:actuate="onLoad"/>
            <xsl:apply-templates select="svg:desc"/>
            <xsl:message>New image for <xsl:value-of select="@draw:name"/>: <xsl:value-of select="$new-src"/></xsl:message>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text:tab[not(ancestor::text:index-body)]">
        <text:s text:c="4"/>
    </xsl:template>

    <!-- default: copy everything. -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
