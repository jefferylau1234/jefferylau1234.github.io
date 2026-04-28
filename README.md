# Jeffery Lau's Showcase
Hi! Here are some projects and documents that I want to showcase for your reference based on my knowledge and skill sets. Feel free to take a look and try it.
Thank you and have fun! 😉

# Software Engineering Project
https://github.com/CSCI3100-Project-Group-9/CSCI3100-Project-Group-9/tree/main





# CENG 3420 Final Review 

## 1. Address Translation & Memory Hierarchy (TLB + Page + Cache)

**Key ideas:**
- Virtual address → VPN + VPO → TLB lookup → PPN → Physical address → Cache access
- Page size determines the number of offset bits.
- TLB is a cache for address translations; caches are usually indexed/tagged with physical addresses.
- TLB miss ≠ page fault; page fault requires disk access.

**Example (new):**
> Page size = 8 KB, virtual address = 32 bits, physical address = 28 bits.  
> The TLB is 4‑way set associative with 64 total entries.  
> **Questions:**  
> 1. How many bits are in the VPN?  
> 2. How many bits for TLB index and TLB tag?  
> 3. If virtual page number `0x12AB` maps to physical page number `0x345`, what is stored in the TLB entry?

---

## 2. Cache Capacity Calculation (General Formula)

**Key ideas:**
- Total bits = data bits + (tag bits + valid bit + dirty bit) × number of blocks
- Direct‑mapped: number of blocks = cache size / block size
- Fully associative: only one set; tag covers all upper address bits
- Set‑associative: number of sets = number of blocks / associativity

**Example (new):**
> A cache holds 16 KB of data, block size = 64 bytes, 32‑bit addresses, **2‑way set associative**.  
> **Compute:**  
> ① Number of blocks  
> ② Number of sets  
> ③ Tag bits per block  
> ④ Total storage bits (including data, tags, and valid bits)

---

## 3. Single‑Cycle Datapath Extension (New Instruction)

**Key ideas:**
- Steps to add a new instruction:  
  - What operation is needed? (ALU, memory access, immediate generation)  
  - Can existing blocks be reused? (register file, ALU, data memory)  
  - Do we need new control signals? (e.g., new ALU opcode, new mux selector)  
  - Draw the modified datapath (only changed parts)

**Example (new):**
> Add a new instruction `ldx rd, (rs1, rs2)` – load a doubleword from address `rs1 + rs2` into `rd`.  
> **Questions:**  
> 1. Does this need a new ALU mode?  
> 2. Does the immediate extension unit need modification?  
> 3. What new signals does the control unit produce?  
> 4. Draw the modified part of the datapath (only the additions).

---

## 4. Heterogeneous System Performance Comparison

**Key ideas:**
- Execution time = total instructions / (frequency × IPC)
- Partition tasks: floating‑point → cores with FPU, simple integer → fast small cores, vector work → vector unit
- Synchronization overhead is ignored in this comparison.

**Example (new):**
> Three cores:  
> - Core A: 1.5 GHz, 2 IPC, has FPU  
> - Core B: 2.5 GHz, 1 IPC, no FPU  
> - Core C: 500 MHz, 8 vector operations per cycle (FP multiply‑add only)  
> Task: SAXPY on 1 million single‑precision floats: `y[i] = a * x[i] + y[i]`  
> **Question:** How would you partition the work between two cores to minimize execution time? Estimate the time (ignore data movement and loop overhead).
