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

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel

/**
 * Tests for applying EfferentCouplingMetric at the class level
 *
 * @see EfferentCouplingMetric_PackageTest
 *
 * @author Chris Mair
 */
class EfferentCouplingMetric_ClassTest extends AbstractMetricTestCase {

    static metricClass = EfferentCouplingMetric

    void testMetricProperties() {
        assert metric.baseLevel == MetricLevel.PACKAGE
        assert metric.name == 'EfferentCoupling'
        assert metric.ignorePackageNames == null
    }

    // Tests for applyToClass()

    void testApplyToClass_NoExternalPackageReferences() {
        final SOURCE = """
            class MyClass {
                int myValue
            }
        """
        assertApplyToClass(SOURCE, [])
    }

    void testApplyToClass_Imports() {
        final SOURCE = """
            import com.example.util.ExampleUtil
            import com.example.other.*
            import static com.example.helper.MyHelper.VALUE
            import static com.example.Main.*
            class MyClass {
                int myValue = ExampleUtil.calculate()
            }
        """
        assertApplyToClass(SOURCE, ['com.example.util', 'com.example.other', 'com.example.helper', 'com.example'])
    }

    void testApplyToClass_MethodReturnType() {
        final SOURCE = """
            interface MyInterface {
                com.example.helper.MyHelper doStuff()
            }
        """
        assertApplyToClass(SOURCE, ['com.example.helper'])
    }

    void testApplyToClass_MethodParameters() {
        final SOURCE = """
            class MyClass {
                void doStuff(com.example.helper.MyHelper helper, int count, org.other.Thing thing) {}
            }
        """
        assertApplyToClass(SOURCE, ['com.example.helper', 'org.other'])
    }

    void testApplyToClass_ConstructorParameters() {
        final SOURCE = """
            class MyClass {
                public MyClass(com.example.helper.MyHelper helper, int count, org.other.Thing thing) {
                    super(thing)
                }
            }
        """
        assertApplyToClass(SOURCE, ['com.example.helper', 'org.other'])
    }

    void testApplyToClass_ClosureParameters() {
        final SOURCE = """
            class MyClass {
                def doStuff = { com.example.helper.MyHelper helper, int count, org.other.Thing thing -> }
            }
        """
        assertApplyToClass(SOURCE, ['com.example.helper', 'org.other'])
    }

    void testApplyToClass_CastExpression() {
        final SOURCE = """
            class MyClass {
                def x = other as org.other.Thing
            }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_ConstructorCall() {
        final SOURCE = """
            class MyClass {
                def instance = new org.other.OtherClass()
            }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_ConstructorCallParameter() {
        final SOURCE = """
            class MyClass {
                def instance = new MyClass(org.other.OtherClass)
            }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_ExtendsSuperclass() {
        final SOURCE = """
            class MyClass extends org.other.OtherClass { }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_ExtendsInterface() {
        final SOURCE = """
            class MyInterface extends org.other.OtherInterface { }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_ImplementsInterface() {
        final SOURCE = """
            class MyClass implements org.other.framework.OtherInterface, com.example.ExampleInterface { }
        """
        assertApplyToClass(SOURCE, ['org.other.framework', 'com.example'])
    }

    void testApplyToClass_Field() {
        final SOURCE = """
            class MyClass {
                org.other.OtherClass otherClass
            }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_Variable() {
        final SOURCE = """
            class MyClass {
                def doStuff() {
                    org.other.OtherClass otherClass = null
                }
            }
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_IgnoresThisReferences() {
        final SOURCE = """
            class MyClass {
                private final int someValue
                MyClass() {
                    this.someValue = 23
                }
            }
        """
        assertApplyToClass(SOURCE, [])
    }

    void testApplyToClass_ClassNameWithinExpression() {
        final SOURCE = '''
            if (value.class == org.bad.BadClass) { }
            println "isClosure=${value instanceof org.other.OtherClass}"
            def count = com.example.Helper.getCount()

            println myVariable.prop.value       // NOT a class/package name
            println child.name                  // NOT a class/package name
            println this.field                  // NOT a class/package name
        '''
        assertApplyToClass(SOURCE, ['org.bad', 'org.other', 'com.example'])
    }

    void testApplyToClass_StaticMemberOfClass() {
        final SOURCE = '''
            println org.math.Constants.PI
            println org.net.RMI     // make sure it does not think this is a static member reference
        '''
        assertApplyToClass(SOURCE, ['org.math', 'org.net'])
    }

    void testApplyToClass_IgnoresSyntheticFieldsAndMethods() {
        final SOURCE = """
            def x = org.other.MyClass
        """
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_IgnorePackageReferencesFromSamePackage() {
        final SOURCE = """
            package com.example
            class MyClass {
                com.example.Thing thing
            }
        """
        assertApplyToClass(SOURCE, [])
    }

    void testApplyToClass_IgnoresJavaAndGroovyPackages() {
        final SOURCE = """
            class MyClass {
                java.net.Socket socket
                def x = new groovy.sql.Sql()
            }
        """
        assertApplyToClass(SOURCE, [])
    }

    void testApplyToClass_IgnorePackageNames() {
        final SOURCE = """
            class MyClass {
                com.example.Thing thing
                org.other.OtherClass otherClass
                def x = new org.other.handler.MyHandler()
            }
        """
        metric.ignorePackageNames = 'com.example,org.example,org.other.*'
        assertApplyToClass(SOURCE, ['org.other'])
    }

    void testApplyToClass_Enum() {
        final SOURCE = """
            enum MyEnum { ONE, TWO, THREE }
        """
        assertApplyToClass(SOURCE, [])
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void assertApplyToClass(String source, List referencedPackages) {
        assertApplyToClass(source, [referencedPackages:referencedPackages as Set])
    }

}