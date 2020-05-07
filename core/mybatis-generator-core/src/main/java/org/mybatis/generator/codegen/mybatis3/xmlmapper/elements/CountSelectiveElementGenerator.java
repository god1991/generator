/**
 * Copyright 2006-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

public class CountSelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public CountSelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "countSelective")); //$NON-NLS-1$

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));
        answer.addAttribute(new Attribute("resultType", //$NON-NLS-1$
               "java.lang.Integer"));
        context.getCommentGenerator().addComment(answer);

//        GeneratedKey gk = introspectedTable.getGeneratedKey();
//        if (gk != null) {
//            introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
//                // if the column is null, then it's a configuration error. The
//                // warning has already been reported
//                if (gk.isJdbcStandard()) {
//                    answer.addAttribute(new Attribute("useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
//                    answer.addAttribute(
//                            new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
//                    answer.addAttribute(
//                            new Attribute("keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
//                } else {
//                    answer.addElement(getSelectKey(introspectedColumn, gk));
//                }
//            });
//        }

        StringBuilder sb = new StringBuilder();

        sb.append("select count(*) from "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        XmlElement dynamicElement = new XmlElement("trim"); //$NON-NLS-1$
        dynamicElement.addAttribute(new Attribute("prefix", "WHERE ("));
        dynamicElement.addAttribute(new Attribute("prefixOverrides", "AND |OR "));
        dynamicElement.addAttribute(new Attribute("suffix", ")"));
        answer.addElement(dynamicElement);

        int num = 0;
        for (IntrospectedColumn introspectedColumn :
                ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            if (num != 0) {
                sb.append(" AND ");
            }
            num++;
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));

            isNotNullElement.addElement(new TextElement(sb.toString()));

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty() + "Clause");
            sb.append(" != null"); //$NON-NLS-1$
            sb.append(" and " + introspectedColumn.getJavaProperty());
            sb.append(" == null"); //$NON-NLS-1$
            isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            if (num != 0) {
                sb.append(" AND ");
            }

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterTrueClause(introspectedColumn, null));


            isNotNullElement.addElement(new TextElement(sb.toString()));
            num++;

        }

        if (context.getPlugins().sqlMapInsertSelectiveElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
