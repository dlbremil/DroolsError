package com.sample;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.MBeansOption;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.sample.facts.Fact1;
import com.sample.facts.Fact2;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

	private static KnowledgeBaseConfiguration kbConfig;
	private static KnowledgeBase kbase;
	protected static StatefulKnowledgeSession kSession;
	
    public static final void main(String[] args) {
        try {
            // load up the knowledge base
        	kbConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    		kbConfig.setOption(MBeansOption.ENABLED);

    		kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConfig);
    		kSession = kbase.newStatefulKnowledgeSession();
    		readKnowledgeBase();

            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(kSession, "test");
            // go !
           
            Fact1 f1 = new Fact1();
            f1.setId("id1");
            
            Fact2 f2 = new Fact2();
            f2.setId(f1.getId());

            kSession.insert(f1);
            kSession.insert(f2);

            kSession.fireAllRules();

           kbase.removeKnowledgePackage("com.sample.test1");
            
           
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
      
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase);
        kbuilder.add(ResourceFactory.newClassPathResource("DoubleRule.drl"), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

 }
