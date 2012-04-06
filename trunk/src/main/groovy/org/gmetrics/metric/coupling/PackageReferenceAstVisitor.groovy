/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.gmetrics.metric.coupling

import static org.gmetrics.util.ClassNameUtil.*
import static org.gmetrics.util.AstUtil.isFromGeneratedSourceCode

import org.gmetrics.metric.AbstractAstVisitor
import org.gmetrics.util.ImportUtil
import org.gmetrics.util.WildcardPattern
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*

/**
 * AstVisitor that checks for references to other packages
 *
 * @author Chris Mair
 */
class PackageReferenceAstVisitor extends AbstractAstVisitor {

    final Set otherPackages = []

    private WildcardPattern wildcard
    private WildcardPattern javaAndGroovyWildcard = new WildcardPattern('java.*,groovy.*')
    private String thisPackageName

    PackageReferenceAstVisitor(String ignorePackageNames) {
        wildcard = new WildcardPattern(ignorePackageNames, false)
    }

    @Override
    void visitClass(ClassNode node) {
        thisPackageName = node.packageName
        def superClassName = node.superClass.name
        checkTypeName(superClassName, node)
        node.interfaces.each { interfaceNode ->
            checkTypeName(interfaceNode.name, node)
        }
        super.visitClass(node)
    }

    @Override
    void visitField(FieldNode node) {
        checkType(node)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression node) {
        checkType(node)
        super.visitConstructorCallExpression(node)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        checkType(expression)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        checkTypeName(node.returnType.name, node)
        node.parameters.each { parameter ->
            checkType(parameter)
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        expression.parameters.each { parameter ->
            checkType(parameter)
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        checkTypeName(expression.type.name, expression)
        super.visitCastExpression(expression)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        checkType(expression)
        super.visitClassExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        if (isClassName(expression.text)) {
            checkTypeName(expression.text, expression)
        }
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitImports(ModuleNode node) {
        def allImports = node.imports + node.starImports + node.staticImports.values() + node.staticStarImports.values()
        allImports?.each { importNode ->
            def parentPackage = ImportUtil.packageNameForImport(importNode)
            checkPackageName(parentPackage, importNode)
        }
        super.visitImports(node)
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    private void checkType(node) {
        checkTypeName(node.type.name, node)
    }

    private void checkTypeName(String typeName, node) {
        def parentPackage = parentPackageName(typeName)
        checkPackageName(parentPackage, node)
    }

    private void checkPackageName(String packageName, node) {
        def notGenerated = !isFromGeneratedSourceCode(node) || node instanceof ImportNode
        if (packageName && isValidPackageReference(packageName) && notGenerated) {
            otherPackages << packageName
        }
    }

    private boolean isValidPackageReference(String packageName) {
        return !wildcard.matches(packageName) && !javaAndGroovyWildcard.matches(packageName) && packageName != thisPackageName
    }

}