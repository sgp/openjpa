/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.persistence.relations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.textui.TestRunner;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.persistence.test.SQLListenerTestCase;


public class TestInverseEagerSQL
    extends SQLListenerTestCase {

    public void setUp() {
        setUp(Customer.class, Customer.CustomerKey.class, Order.class, 
        	EntityAInverseEager.class, EntityA1InverseEager.class, EntityA2InverseEager.class, 
        	EntityBInverseEager.class, EntityCInverseEager.class, EntityDInverseEager.class);
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Customer.CustomerKey ck = new Customer.CustomerKey("USA", 1);
        Customer c = new Customer();
        c.setCid(ck);
        c.setName("customer1");
        em.persist(c);
        
        for (int i = 0; i < 4; i++) {
            Order order = new Order();
            order.setCustomer(c);
            em.persist(order);
        }

        EntityAInverseEager a = new EntityAInverseEager("a");
        em.persist(a);
        
        EntityA1InverseEager a1 = new EntityA1InverseEager("a1");
        em.persist(a1);
        
        EntityA2InverseEager a2 = new EntityA2InverseEager("a2");
        em.persist(a2);

        for (int i = 0; i < 4; i++) {
            EntityBInverseEager b = new EntityBInverseEager("b" + i);
            a.addB(b);
            b.setA(a);
            em.persist(b);
        }
        
        for (int i = 4; i < 8; i++) {
            EntityBInverseEager b = new EntityBInverseEager("b" + i);
            a1.addB(b);
            b.setA(a1);
            em.persist(b);
        }

        for (int i = 8; i < 12; i++) {
            EntityBInverseEager b = new EntityBInverseEager("b" + i);
            a2.addB(b);
            b.setA(a2);
            em.persist(b);
        }
        
        for (int i = 0; i < 4; i++) {
            EntityCInverseEager c1 = new EntityCInverseEager("c"+i, i, i);
            em.persist(c1);

            EntityDInverseEager d1 = new EntityDInverseEager("d"+i, "d"+i, i, i);
            em.persist(d1);

            c1.setD(d1);
            d1.setC(c1);
        }
        
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    public void testOneToManyInverseEagerQuery() {
        sql.clear();

        OpenJPAEntityManager em = emf.createEntityManager();
        OpenJPAQuery q = em.createQuery("SELECT c FROM Customer c ");
        List<Customer> res = q.getResultList(); 

        assertEquals(1, res.size());

        for (int i = 0; i < res.size(); i++) {
            Customer c = (Customer)res.get(i);
            Collection<Order> orders = c.getOrders();
            for (Iterator<Order> iter=orders.iterator(); iter.hasNext();) {
                Order order = (Order)iter.next();
                assertEquals(order.getCustomer(), c);
            }
        }
        
        assertEquals(2, sql.size());
        em.close();
    }

    public void testOneToOneInverseEagerQuery() {
        sql.clear();

        OpenJPAEntityManager em = emf.createEntityManager();
        String query = "select c FROM EntityCInverseEager c";
        Query q = em.createQuery(query);
        List<EntityCInverseEager> res = q.getResultList();
        assertEquals(4, res.size());

        for (int i = 0; i < res.size(); i++) {
            EntityCInverseEager c = (EntityCInverseEager)res.get(i);
            EntityDInverseEager d = c.getD();
            assertEquals(c, d.getC());
        }

        assertEquals(1, sql.size());
        em.close();
    }

    public void testOneToManyInheritanceQuery() {
        sql.clear();

        OpenJPAEntityManager em = emf.createEntityManager();
        String query = "select a FROM EntityA1InverseEager a";
        Query q = em.createQuery(query);
        List list = q.getResultList();
        assertEquals(1, list.size());
        for (int i = 0; i < list.size(); i++) {
            EntityA1InverseEager a1 = (EntityA1InverseEager)list.get(i);
            Collection<EntityBInverseEager> listB = a1.getListB();
            assertEquals(4, listB.size());
            for (Iterator iter=listB.iterator(); iter.hasNext();) {
                EntityBInverseEager b = (EntityBInverseEager)iter.next();
                EntityAInverseEager a = b.getA();
                assertEquals(a1, a);
            }
        }
        assertEquals(3, sql.size());
        sql.clear();

        query = "select a FROM EntityA2InverseEager a";
        q = em.createQuery(query);
        list = q.getResultList();
        assertEquals(1, list.size());
        for (int i = 0; i < list.size(); i++) {
            EntityA2InverseEager a2 = (EntityA2InverseEager)list.get(i);
            Collection listB = a2.getListB();
            assertEquals(4, listB.size());
            for (Iterator iter=listB.iterator(); iter.hasNext();) {
                EntityBInverseEager b = (EntityBInverseEager)iter.next();
                EntityAInverseEager a = b.getA();
                assertEquals(a2, a);
            }
        }
        assertEquals(3, sql.size());
        sql.clear();
        
        query = "select a FROM EntityAInverseEager a";
        q = em.createQuery(query);
        list = q.getResultList();
        assertEquals(3, list.size());
        for (int i = 0; i < list.size(); i++) {
            EntityAInverseEager a0 = (EntityAInverseEager)list.get(i);
            Collection listB = a0.getListB();
            assertEquals(4, listB.size());
            for (Iterator iter=listB.iterator(); iter.hasNext();) {
                EntityBInverseEager b = (EntityBInverseEager)iter.next();
                EntityAInverseEager a = b.getA();
                assertEquals(a0, a);
            }
        }
        
        assertEquals(2, sql.size());
        em.close();
    }

    public static void main(String[] args) {
        TestRunner.run(TestInverseEagerSQL.class);
    }
}

