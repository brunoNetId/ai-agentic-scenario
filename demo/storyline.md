# Demo Storyline & Flow

This README describes the step-by-step flow of the multi-agent AI demo, guiding you through preparation, ingestion, chat-driven interactions, and advanced scenarios. Use this as a reference when running or presenting the demo.

---

## 1. Preparation

Before starting the demo, ensure the following:

- **Empty buckets & data:** Clear out any previous data from S3 buckets, databases, and vector stores.
- **Reset Name fields in Data Mapper:** Ensure the Data Mapper is set to its initial state.
- **Open the tunnel (port-forward):** Use `oc port-forward` to allow local access to remote services (e.g., PostgreSQL).
- **Ensure models are loaded:** Make sure all required LLMs are running and ready.
- **Dashboards loaded:** Open all relevant dashboards (e.g., Milvus UI, OCP Console).
- **Agents down:** Stop any running agent applications to start from a clean state.
- **OCP login is fresh:** Log in to OpenShift in both the console and terminal.

**Quick checks:**
- OCP Console ready on PostgreSQL
- Milvus UI ready on data

---

## 2. Demo Start

- **Show the Camel route for ingestion:** Begin by presenting the Camel integration route responsible for ingesting data.

---

## 3. Ingestion Phase

- **Show slides:** Briefly introduce the process with slides.
- **Show Kaoto process and Data Mapper:** Demonstrate the Kaoto workflow and how data mapping is configured.

### Live Mapping Change

- Edit the Data Mapper live to show how seller and client names are mapped from the EDI file.

  ```xml
  <Seller>
      <Name>
          <xsl:value-of select="/X12_850/Header/N1Loop[1]/N1/N1-02"/>
      </Name>
  </Seller>
  <Client>
      <Name>
          <xsl:value-of select="/X12_850/Header/N1Loop[2]/N1/N1-02"/>
      </Name>
  </Client>
  ```

- **Drop EDI file:** Upload an X12 EDI file to the system.
- **Check invoice in S3:** Verify the invoice appears in the S3 bucket.
- **Check Milvus and Database:** Confirm the data is ingested into Milvus (vector DB) and the relational database.

- **Drop invoices:** Upload additional invoices and check their ingestion status.

---

## 4. Chat-Driven Interaction

- **Bulk ingestion:** Drop the full invoice collection to save demo time.

### Example Interactions

```
You: Please get me invoice 61356291  
LLM: (shows invoice)

You: There is a mistake in invoice 61356291, the customer has ordered 4 units of wine glasses, not 5, please fix it  
LLM: The invoice has been successfully amended. Quantity has been corrected from 5 to 4.

You: Show me invoice 61356291  
LLM: (shows amended invoice)

You: Please get me id and details about the current active promotions we have  
LLM: Current active promotions:  
- ID: 100, Type: Vouchers, Description: Wine related products  
- ID: 101, Type: Loyalty, Description: Loyalty points  
- ID: 102, Type: Two-for-One, Description: Buy 1 book, get 1 free

You: Please apply promotion 100 to the customer in invoice 61356291  
LLM: Promotion 100 has been applied to the customer in invoice 61356291.

You: In fact, please find me customers interested in wine products  
LLM: Here are the customer IDs interested in wine products: 61356291, 49565075, 34911755, 54212825, 67095041.

You: Please reward customers of invoices 61356291, 49565075, 34911755, 54212825 with promotion 1  
LLM: All promotions have been successfully awarded.
```

- **Show Kaoto Agent's code:** Demonstrate the agent's code for retrieving promotions.
- **Show outgoing email:** Display the email sent to the customer after applying a promotion.

---

## 5. Planned Execution (Advanced Scenario)

- **Launch a complex prompt:**

  ```
  There is a mistake in invoice 67095041, the tax applied is incorrect, it should be 5 percent instead of 10, please fix it. Also, please find the current promotions programs available, and award a promotion to the customer related to wine products. Actually, find other customers interested in wine, and apply the same promotion.
  ```

- **Show side-by-side Dashboard & Chat:** Visualize the system's response and workflow in real time.

---

## 6. Human in the Loop

- **Drop transcript:** Read and process a transcript to demonstrate human oversight and intervention.
- **Show Dashboard & Transcript:** Display both the dashboard and transcript views for transparency.

---

## Summary

This storyline guides you through a full demo cycle, from setup and ingestion to advanced, chat-driven automation and human-in-the-loop scenarios. Use it to ensure a smooth and comprehensive demonstration of the multi-agent AI system.