/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.example.restlet.jdbc;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRouteConfig extends SingleRouteCamelConfiguration implements InitializingBean {

    /**
     * Returns the CamelContext which support Spring
     */
    @Override
    protected CamelContext createCamelContext() throws Exception {
        return new SpringCamelContext(getApplicationContext());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("restlet:/persons?restletMethod=POST")
                        .setBody(simple("insert into person(firstName, lastName) values('${header.firstName}','${header.lastName}')"))
                        .to("log:out")
                        .setBody(simple("select * from person where id in (select max(id) from person)"))
                        .to("log:out");

                from("restlet:/persons/{personId}?restletMethods=GET,PUT,DELETE")
                        .choice()
                        .when(simple("${header.CamelHttpMethod} == 'GET'"))
                        .setBody(simple("select * from person where id = ${header.personId}"))
                        .when(simple("${header.CamelHttpMethod} == 'PUT'"))
                        .setBody(simple("update person set firstName='${header.firstName}', lastName='${header.lastName}' where id = ${header.personId}"))
                        .when(simple("${header.CamelHttpMethod} == 'DELETE'"))
                        .setBody(simple("delete from person where id = ${header.personId}"))
                        .otherwise()
                        .stop()
                        .end()
                        .to("log:out");

                from("restlet:/persons?restletMethod=GET")
                        .setBody(simple("select * from person"))
                        .to("log:out");
            }
        };
    }
}

