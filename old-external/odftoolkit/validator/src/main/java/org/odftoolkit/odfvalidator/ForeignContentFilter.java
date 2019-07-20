/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * 
 * Use is subject to license terms.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/

package org.odftoolkit.odfvalidator;

import java.util.HashSet;
import java.util.Vector;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

class ForeignContentFilter extends XMLFilterImpl {

    private static final String OFFICE_NAMESPACE_URI = OdfDocumentNamespace.OFFICE.getUri();
    private static final String TEXT_NAMESPACE_URI = OdfDocumentNamespace.TEXT.getUri();

    private static final String H = "h";
    private static final String P = "p";

    private static final String PROCESS_CONTENT = "process-content";
    private static final String TRUE = "true";

    // Set of ODF namespace URIs
    private static HashSet<String> m_aODFNamespaceSet = null;

    // This list contains a boolean for all ancestor foreign elements.
    // That boolean values specifies whether the element content is processed or not.
    private Vector<Boolean> m_aAlienElementProcessContents = null;

    // This list contains a boolean for all ancestor elements.
    // That boolean values specifies whether the element is a text:h ot text:p
    // element itself or has a text:h or text:p ancestor element.
    private Vector<Boolean> m_aParagraphAncestorElements = null;

    private ForeignContentListener m_aForeignContentListener = null;

    private OdfVersion m_aVersion = null;
    private Logger m_aLogger;

    /** Creates a new instance of NamespaceFilter */
    ForeignContentFilter( Logger aLogger, OdfVersion aVersion, ForeignContentListener aForeignContentListener ) {
        m_aLogger = aLogger;
        m_aVersion = aVersion;
        m_aForeignContentListener = aForeignContentListener;

        m_aAlienElementProcessContents = new Vector<Boolean>();
        m_aParagraphAncestorElements = new Vector<Boolean>();
        
        m_aODFNamespaceSet = new HashSet<String>();
        m_aODFNamespaceSet.add( OdfDocumentNamespace.OFFICE.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.STYLE.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.TEXT.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.TABLE.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.DRAW.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.FO.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.DC.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.META.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.NUMBER.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.SVG.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.CHART.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.DR3D.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.FORM.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.PRESENTATION.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.SMIL.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.CONFIG.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.SCRIPT.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.XLINK.getUri() );
        m_aODFNamespaceSet.add( OdfDocumentNamespace.XFORMS.getUri() );
        if( m_aVersion.compareTo( OdfVersion.V1_2 ) >= 0 )
        {
            m_aODFNamespaceSet.add( OdfDocumentNamespace.XHTML.getUri() );
            m_aODFNamespaceSet.add( OdfDocumentNamespace.GRDDL.getUri() );
            m_aODFNamespaceSet.add( OdfDocumentNamespace.DB.getUri() );
            m_aODFNamespaceSet.add( javax.xml.XMLConstants.XML_NS_URI );
        }
    }


    @Override
    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
        if( isAlienNamespace(aUri) )
        {
            m_aAlienElementProcessContents.removeElementAt( m_aAlienElementProcessContents.size()-1 );
        }
        else
        {
            if( isProcessContent() )
            {
                m_aParagraphAncestorElements.removeElementAt(m_aParagraphAncestorElements.size()-1);
                super.endElement(aUri,aLocalName,aQName);
            }
        }
    }


    @Override
    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts) throws SAXException
    {
        boolean bProcessContent = isProcessContent();
        
        if( isAlienNamespace(aUri) )
        {
            if( bProcessContent )
            {
                String aProcessContentValue = aAtts.getValue( OFFICE_NAMESPACE_URI, PROCESS_CONTENT );
                if( m_aVersion.compareTo( OdfVersion.V1_2 ) >= 0 )
                {
                    bProcessContent = aProcessContentValue != null
                                        ? aProcessContentValue.equals(TRUE)
                                        : hasParagraphAncestorElement();
                }
                else
                {
                    bProcessContent = aProcessContentValue == null || aProcessContentValue.equals(TRUE);
                }

                if( m_aForeignContentListener!= null )
                    m_aForeignContentListener.foreignElementDetected(aUri, aLocalName, aQName, aAtts);
                m_aLogger.logInfo( String.format("extension element <%s> found, element is ignored, element content is %s.",aQName,bProcessContent?"processed":"is ignored"), false);
            }
            m_aAlienElementProcessContents.addElement( bProcessContent );
        }
        else if( bProcessContent )
        {
            Attributes aOldAtts = aAtts;

            AttributesImpl aNewAtts = null;
            int i = aOldAtts.getLength();
            while( i>0 ) {
                --i;
                String aAttrUri = aOldAtts.getURI(i);
                if ( isAlienNamespace( aAttrUri ) ) {
                    if (aNewAtts == null) {
                        aNewAtts = new AttributesImpl(aOldAtts);
                        aAtts = aNewAtts;
                    }
                    if( m_aForeignContentListener!= null )
                        m_aForeignContentListener.foreignAttributeDetected(aAttrUri, aOldAtts.getLocalName(i), aOldAtts.getQName(i), aOldAtts.getValue(i));
                    m_aLogger.logInfo( String.format("extension attribute '%s' of element <%s> found and ignored.",aAtts.getQName(i),aQName), false);
                    aNewAtts.removeAttribute(i);
                }
            }

            boolean bParagraphAncestor = hasParagraphAncestorElement();
            bParagraphAncestor |= ((aLocalName.equals(P) || aLocalName.equals(H)) && aUri.equals(TEXT_NAMESPACE_URI));
            m_aParagraphAncestorElements.add(bParagraphAncestor);

            super.startElement(aUri, aLocalName, aQName, aAtts);
        }
    }
    
    

    private boolean isAlienNamespace( String aUri )
    {
        return !m_aODFNamespaceSet.contains(aUri);
    }

    private boolean isProcessContent()
    {
        return m_aAlienElementProcessContents.isEmpty() ? true : m_aAlienElementProcessContents.lastElement();
    }

    private boolean hasParagraphAncestorElement()
    {
        return m_aParagraphAncestorElements.isEmpty() ? false : m_aParagraphAncestorElements.lastElement();
    }

    @Override
    public void characters(char[] aChars, int nStart, int nLength) throws SAXException {
        if( isProcessContent() )
            super.characters(aChars, nStart, nLength);
    }
    
}
