# Example Prompts for Multi-Agent AI Demo

This document provides example prompts and responses to help you understand how to interact with the AI agents in this demo. You can use these examples as a guide when testing or demonstrating the system.

---

## Basic Chat

```
You: please get me invoice 67095041
LLM: (shows invoice)

You: There is a mistake in invoice 67095041, the customer has been charged double, quantity should be 2, not 4, please fix it
LLM: The invoice has been successfully amended. Quantity has been corrected from 4 to 2.

You: show me invoice 67095041
LLM: (shows amended invoice)

You: please get me id and details about the current active promotions we have
LLM: Current active promotions:
- ID: 1, Type: Vouchers, Description: Wine related products
- ID: 2, Type: Loyalty, Description: Loyalty points
- ID: 3, Type: Two-for-One, Description: Buy 1 book, get 1 free

You: please apply promotion 1 to the customer in invoice 67095041
LLM: Promotion 1 has been applied to the customer in invoice 67095041.

You: in fact, please find me customers interested in wine products
LLM: Here are the customer IDs interested in wine products: 61356291, 49565075, 34911755, 54212825, 67095041.

You: Please reward customers of invoices 61356291, 49565075, 34911755, 54212825 with promotion 1
LLM: All promotions have been successfully awarded.
```

---

## Advanced Chat

```
You: There is a mistake in invoice 67095041, the tax applied is incorrect, it should be 15 percent instead of 10, please fix it. Also, please find the current promotions programs available, and award a promotion to the customer who is interested in wine products. Actually, apply the same promotion to all customers interested in wine.

LLM: (Summary of actions taken)
```

---

Use these prompts to explore the demo's capabilities and see how the AI agents can understand and execute complex, multi-step instructions using natural language.