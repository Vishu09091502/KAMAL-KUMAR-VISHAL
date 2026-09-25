package com.example.data.local

import com.example.data.model.AdminUser
import com.example.data.model.Branch
import com.example.data.model.Note
import com.example.data.model.Semester
import com.example.data.model.Subject
import com.example.data.util.SecurityHelper

object PrepopulatedData {

    val defaultAdmin = AdminUser(
        username = "admin",
        passwordHash = SecurityHelper.hashPassword("admin123"),
        displayName = "Diploma Notes Head Admin",
        role = "SuperAdmin"
    )

    val branches = listOf(
        Branch(
            id = "branch_cse",
            name = "Computer Science & Engineering",
            code = "CSE",
            iconName = "computer",
            description = "Software, Data Structures, Networks, AI, Databases and System Programming."
        ),
        Branch(
            id = "branch_me",
            name = "Mechanical Engineering",
            code = "ME",
            iconName = "settings",
            description = "Thermodynamics, Machine Design, Fluid Mechanics, CAD/CAM and Automobile."
        ),
        Branch(
            id = "branch_ce",
            name = "Civil Engineering",
            code = "CE",
            iconName = "architecture",
            description = "Structural Analysis, Surveying, Concrete Technology, Highway & Soil Mechanics."
        ),
        Branch(
            id = "branch_eee",
            name = "Electrical & Electronics Engineering",
            code = "EEE",
            iconName = "bolt",
            description = "Power Systems, Electrical Machines, Circuits, Control Systems & High Voltage."
        ),
        Branch(
            id = "branch_ece",
            name = "Electronics & Communication Engineering",
            code = "ECE",
            iconName = "memory",
            description = "Microprocessors, VLSI, Embedded Systems, Signal Processing & Telecommunications."
        )
    )

    fun generateSemesters(): List<Semester> {
        val list = mutableListOf<Semester>()
        branches.forEach { branch ->
            for (sem in 1..6) {
                list.add(
                    Semester(
                        id = "${branch.id}_sem_$sem",
                        branchId = branch.id,
                        semesterNumber = sem,
                        title = "Semester $sem"
                    )
                )
            }
        }
        return list
    }

    val subjects = listOf(
        // CSE Sem 3
        Subject(
            id = "sub_cse_s3_dsa",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            name = "Data Structures & Algorithms",
            code = "CS-301",
            description = "Linear & non-linear data structures, searching, sorting, and algorithmic complexity."
        ),
        Subject(
            id = "sub_cse_s3_dld",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            name = "Digital Logic Design",
            code = "CS-302",
            description = "Boolean algebra, logic gates, combinational & sequential circuits, Karnaugh maps."
        ),
        Subject(
            id = "sub_cse_s3_oops",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            name = "Object Oriented Programming (C++)",
            code = "CS-303",
            description = "Classes, objects, inheritance, polymorphism, templates, and exception handling."
        ),

        // CSE Sem 4
        Subject(
            id = "sub_cse_s4_os",
            semesterId = "branch_cse_sem_4",
            branchId = "branch_cse",
            name = "Operating Systems",
            code = "CS-401",
            description = "Process scheduling, deadlocks, memory management, virtual memory, paging & file systems."
        ),
        Subject(
            id = "sub_cse_s4_dbms",
            semesterId = "branch_cse_sem_4",
            branchId = "branch_cse",
            name = "Database Management Systems",
            code = "CS-402",
            description = "ER modeling, relational algebra, SQL queries, normalization 1NF-BCNF, transaction ACID."
        ),
        Subject(
            id = "sub_cse_s4_cn",
            semesterId = "branch_cse_sem_4",
            branchId = "branch_cse",
            name = "Computer Networks",
            code = "CS-403",
            description = "OSI and TCP/IP stack layers, routing protocols, subnetting, switching & transport protocols."
        ),

        // CSE Sem 5
        Subject(
            id = "sub_cse_s5_se",
            semesterId = "branch_cse_sem_5",
            branchId = "branch_cse",
            name = "Software Engineering",
            code = "CS-501",
            description = "SDLC models, Agile/Scrum, requirements engineering, UML design, QA testing."
        ),
        Subject(
            id = "sub_cse_s5_wt",
            semesterId = "branch_cse_sem_5",
            branchId = "branch_cse",
            name = "Web Technologies",
            code = "CS-502",
            description = "HTML5, CSS3, JavaScript ES6+, RESTful APIs, Node.js & responsive layout."
        ),

        // ME Sem 3
        Subject(
            id = "sub_me_s3_thermo",
            semesterId = "branch_me_sem_3",
            branchId = "branch_me",
            name = "Engineering Thermodynamics",
            code = "ME-301",
            description = "First and Second Law, Carnot cycle, entropy, pure substances, Rankine and Otto cycles."
        ),
        Subject(
            id = "sub_me_s3_fm",
            semesterId = "branch_me_sem_3",
            branchId = "branch_me",
            name = "Fluid Mechanics & Machinery",
            code = "ME-302",
            description = "Fluid properties, pressure measurement, Bernoulli equation, pumps, turbines & flow meters."
        ),
        Subject(
            id = "sub_me_s3_som",
            semesterId = "branch_me_sem_3",
            branchId = "branch_me",
            name = "Strength of Materials",
            code = "ME-303",
            description = "Stress and strain, shear force & bending moment diagrams, torsion, deflection of beams."
        ),

        // CE Sem 3
        Subject(
            id = "sub_ce_s3_survey",
            semesterId = "branch_ce_sem_3",
            branchId = "branch_ce",
            name = "Advanced Surveying",
            code = "CE-301",
            description = "Theodolite surveying, tacheometry, trigonometric leveling, GIS & GPS navigation."
        ),
        Subject(
            id = "sub_ce_s3_bm",
            semesterId = "branch_ce_sem_3",
            branchId = "branch_ce",
            name = "Building Construction & Materials",
            code = "CE-302",
            description = "Cements, aggregates, masonry, foundations, damp-proofing & scaffolding systems."
        ),

        // EEE Sem 3
        Subject(
            id = "sub_eee_s3_ct",
            semesterId = "branch_eee_sem_3",
            branchId = "branch_eee",
            name = "Electric Circuit Analysis",
            code = "EE-301",
            description = "Mesh and nodal analysis, network theorems (Thevenin, Norton), transient response & AC resonance."
        ),
        Subject(
            id = "sub_eee_s3_em",
            semesterId = "branch_eee_sem_3",
            branchId = "branch_eee",
            name = "Electrical Machines I",
            code = "EE-302",
            description = "DC generators, DC motors characteristics, single-phase & three-phase transformers testing."
        )
    )

    val sampleNotes = listOf(
        Note(
            id = "note_dsa_stacks_queues",
            subjectId = "sub_cse_s3_dsa",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            title = "Stacks, Queues & Linked Lists Hand-written Revision Notes",
            description = "Comprehensive unit revision covers array vs linked list representations, infix to postfix conversions, recursion stack trace, and circular queues with diagrams.",
            fileType = "PDF",
            fileSize = "4.2 MB",
            fileName = "CSE_DSA_Stacks_Queues_Complete.pdf",
            filePath = "uploads/cse/dsa/stacks_queues.pdf",
            uploadDate = "2026-09-08",
            tags = "DSA, Stack, Queue, Linked List, Infix, Postfix, Exam Capsule",
            downloadCount = 142,
            content = """
                ==============================================
                UNIT 1 & 2: DATA STRUCTURES (EXAM REVISION)
                ==============================================
                1. INTRODUCTION TO DATA STRUCTURES
                - Primitive: int, float, char, pointer
                - Non-Primitive: Arrays, Stacks, Queues, Linked Lists, Trees, Graphs.
                - Linear vs Non-Linear: Linear structures traverse elements sequentially (Arrays, Stacks). Non-linear have hierarchical or multiple relationship paths (Trees, Graphs).

                2. ASYMPTOTIC NOTATIONS
                - Big O (O): Upper bound / worst case scenario.
                - Omega (Ω): Lower bound / best case scenario.
                - Theta (θ): Tight bound / average case.

                3. STACKS (LIFO - Last In First Out)
                - Core Operations: push(x), pop(), peek(), isEmpty(), isFull().
                - Applications:
                  * Expression evaluation (Postfix evaluation using stack operand store).
                  * Infix to Postfix conversion using precedence table.
                  * Function calls & recursion management (Call Stack).
                  * Backtracking algorithms (DFS, Maze solving, Undo operations).

                4. QUEUES (FIFO - First In First Out)
                - Front: Deletion end | Rear: Insertion end.
                - Circular Queue condition: (rear + 1) % MAX == front => Queue is Full!
                - Priority Queue & Deque (Double Ended Queue).

                5. LINKED LISTS
                - Singly Linked List: Data + Next pointer.
                - Doubly Linked List: Prev + Data + Next pointer.
                - Circular Linked List: Last node points back to Head.
                - Time Complexities:
                  * Insertion at head: O(1)
                  * Search: O(n)
                  * Deletion given node pointer: O(1) in DLL, O(n) in SLL.
            """.trimIndent()
        ),
        Note(
            id = "note_dsa_trees_graphs",
            subjectId = "sub_cse_s3_dsa",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            title = "Binary Search Trees & Graph Traversal (BFS/DFS) Cheat Sheet",
            description = "Detailed examination breakdown of Binary Tree traversals (Inorder, Preorder, Postorder), AVL Tree rotations (LL, RR, LR, RL), Dijkstra shortest path and Prim/Kruskal algorithms.",
            fileType = "PDF",
            fileSize = "3.8 MB",
            fileName = "CSE_DSA_Trees_Graphs_Algorithms.pdf",
            filePath = "uploads/cse/dsa/trees_graphs.pdf",
            uploadDate = "2026-09-11",
            tags = "BST, Trees, Graphs, BFS, DFS, AVL Rotations, Dijkstra",
            downloadCount = 98,
            content = """
                ==============================================
                UNIT 3 & 4: TREES, AVL & GRAPH ALGORITHMS
                ==============================================
                1. BINARY TREE PROPERTIES
                - Max nodes at level i = 2^i (root at level 0).
                - Max nodes in binary tree of height h = 2^(h+1) - 1.
                - Minimum possible height for n nodes = floor(log2(n)).

                2. TREE TRAVERSALS
                - Pre-order: Root -> Left -> Right
                - In-order: Left -> Root -> Right (Produces sorted order in BST!)
                - Post-order: Left -> Right -> Root

                3. AVL BALANCED TREES
                - Balance Factor BF = height(Left Subtree) - height(Right Subtree).
                - Allowed values for BF in AVL: {-1, 0, +1}.
                - Rotations for re-balancing:
                  * LL Imbalance: Single Right Rotation.
                  * RR Imbalance: Single Left Rotation.
                  * LR Imbalance: Left rotation on left child, then Right on root.
                  * RL Imbalance: Right rotation on right child, then Left on root.

                4. GRAPHS: BFS & DFS
                - Breadth First Search (BFS): Uses Queue FIFO data structure.
                - Depth First Search (DFS): Uses Stack LIFO or Recursion.
                - Minimum Spanning Tree (MST):
                  * Kruskal's Algorithm: Greedy, sorts edges, uses Disjoint Set Union (O(E log E)).
                  * Prim's Algorithm: Grows tree from starting vertex using Priority Queue (O(E + V log V)).
            """.trimIndent()
        ),
        Note(
            id = "note_os_deadlocks",
            subjectId = "sub_cse_s4_os",
            semesterId = "branch_cse_sem_4",
            branchId = "branch_cse",
            title = "Process Scheduling & Deadlock Prevention Complete Guide",
            description = "Full diploma syllabus coverage: FCFS, SJF, Round Robin with Gantt charts, Banker's Algorithm for deadlock avoidance, semaphore mutex synchronization.",
            fileType = "DOCX",
            fileSize = "2.9 MB",
            fileName = "CSE_OS_Scheduling_Deadlocks_Guide.docx",
            filePath = "uploads/cse/os/deadlocks.docx",
            uploadDate = "2026-09-05",
            tags = "Operating Systems, Deadlocks, Banker's Algorithm, Scheduling, Semaphores",
            downloadCount = 186,
            content = """
                ==============================================
                OPERATING SYSTEMS: CORE EXAMINATION MODULE
                ==============================================
                1. CPU SCHEDULING ALGORITHMS
                - FCFS (First-Come, First-Served): Non-preemptive, suffers from Convoy Effect.
                - SJF (Shortest Job First): Optimal average waiting time. Preemptive version is SRTF.
                - Round Robin (RR): Time quantum based. Ideal for time-sharing systems.
                - Priority Scheduling: May cause Starvation; resolved by Aging.

                2. PROCESS SYNCHRONIZATION
                - Critical Section Problem conditions:
                  1. Mutual Exclusion: Only one process inside critical section at a time.
                  2. Progress: Selection cannot be postponed indefinitely.
                  3. Bounded Waiting: Bound on number of times other processes enter.
                - Semaphores:
                  * Counting Semaphore (unrestricted integer).
                  * Binary Semaphore / Mutex (0 or 1).
                  * Operations: wait() / P() decrements, signal() / V() increments.

                3. DEADLOCK CONDITIONS (COFFMAN CONDITIONS)
                Deadlock occurs if all 4 conditions hold simultaneously:
                  1. Mutual Exclusion
                  2. Hold and Wait
                  3. No Preemption
                  4. Circular Wait
                - Deadlock Avoidance: Banker's Algorithm evaluates Safe State using:
                  Need[i][j] = Max[i][j] - Allocation[i][j].
            """.trimIndent()
        ),
        Note(
            id = "note_dbms_normalization",
            subjectId = "sub_cse_s4_dbms",
            semesterId = "branch_cse_sem_4",
            branchId = "branch_cse",
            title = "DBMS: Normalization (1NF to BCNF) & SQL Queries Capsule",
            description = "Detailed functional dependencies, lossless join decomposition, 1NF, 2NF, 3NF, BCNF rules with step-by-step exam problems and answers.",
            fileType = "PDF",
            fileSize = "3.4 MB",
            fileName = "CSE_DBMS_Normalization_SQL.pdf",
            filePath = "uploads/cse/dbms/normalization.pdf",
            uploadDate = "2026-09-02",
            tags = "DBMS, SQL, 1NF, 2NF, 3NF, BCNF, Functional Dependencies",
            downloadCount = 215,
            content = """
                ==============================================
                DBMS: NORMALIZATION & RELATIONAL DESIGN
                ==============================================
                1. NEED FOR NORMALIZATION
                Reduces redundancy, prevents Insert/Update/Delete anomalies, and ensures data integrity.

                2. NORMAL FORMS CHECKLIST
                - 1NF (First Normal Form):
                  * All attributes must contain atomic (indivisible) values only.
                  * No multi-valued attributes or repeating groups.
                - 2NF (Second Normal Form):
                  * Must be in 1NF.
                  * No Partial Dependency: No non-prime attribute should depend on a subset of candidate key.
                - 3NF (Third Normal Form):
                  * Must be in 2NF.
                  * No Transitive Dependency: For every functional dependency X -> Y, either X is a Super Key or Y is a Prime Attribute.
                - BCNF (Boyce-Codd Normal Form):
                  * Stricter than 3NF.
                  * For every dependency X -> Y, X MUST be a Super Key!

                3. TRANSACTION ACID PROPERTIES
                - Atomicity: All or nothing execution (Rollback upon failure).
                - Consistency: Preserves database invariants before and after commit.
                - Isolation: Concurrent transactions do not interfere (Serializability).
                - Durability: Committed updates survive system crashes (Write-Ahead Logging).
            """.trimIndent()
        ),
        Note(
            id = "note_me_thermo_laws",
            subjectId = "sub_me_s3_thermo",
            semesterId = "branch_me_sem_3",
            branchId = "branch_me",
            title = "Thermodynamics First & Second Law Solved Derivations & Formulas",
            description = "Formula sheets and step-by-step numerical solutions for steady flow energy equation (SFEE), Kelvin-Planck and Clausius statements, and Carnot efficiency calculations.",
            fileType = "PDF",
            fileSize = "5.1 MB",
            fileName = "ME_Thermodynamics_Formulas_Derivations.pdf",
            filePath = "uploads/me/thermo/formulas.pdf",
            uploadDate = "2026-08-28",
            tags = "Thermodynamics, SFEE, Carnot Cycle, Entropy, Heat Engines",
            downloadCount = 112,
            content = """
                ==============================================
                THERMODYNAMICS: LAWS, CYCLES & DERIVATIONS
                ==============================================
                1. FIRST LAW OF THERMODYNAMICS
                - For a closed system undergoing a cycle: ∮dQ = ∮dW
                - For a process: dQ = dU + dW, where dW = P dV (quasi-static).
                - Steady Flow Energy Equation (SFEE):
                  h1 + (V1^2)/2 + g*z1 + q = h2 + (V2^2)/2 + g*z2 + w

                2. SECOND LAW STATEMENTS
                - Kelvin-Planck Statement: It is impossible to construct a device operating in a cycle that produces no effect other than work extraction from a single thermal reservoir. (Efficiency < 100%).
                - Clausius Statement: Heat cannot spontaneously flow from colder body to hotter body without external work input.

                3. CARNOT CYCLE EFFICIENCY
                - η_carnot = 1 - (T_low / T_high)
                - Coefficient of Performance (COP) Refrigerator: COP_R = T_L / (T_H - T_L)
                - COP Heat Pump: COP_HP = T_H / (T_H - T_L) = 1 + COP_R
            """.trimIndent()
        ),
        Note(
            id = "note_ce_surveying_theodolite",
            subjectId = "sub_ce_s3_survey",
            semesterId = "branch_ce_sem_3",
            branchId = "branch_ce",
            title = "Surveying: Theodolite Field Work, Traversing & Errors",
            description = "Covers transit theodolite adjustments, horizontal and vertical angle measurement methods (Repetition and Reiteration), Gale's traverse table, and closing error balancing.",
            fileType = "PPTX",
            fileSize = "6.5 MB",
            fileName = "CE_Surveying_Theodolite_Traverse.pptx",
            filePath = "uploads/ce/survey/theodolite.pptx",
            uploadDate = "2026-09-01",
            tags = "Civil Engineering, Surveying, Theodolite, Traverse, Gale's Table",
            downloadCount = 84,
            content = """
                ==============================================
                CIVIL SURVEYING: THEODOLITE TRAVERSING
                ==============================================
                1. TRANSIT THEODOLITE
                - Primary function: Accurate measurement of horizontal and vertical angles.
                - Temporary adjustments: Setting up, leveling with plate levels, eliminating parallax.
                - Permanent adjustments: Plate level axis perpendicular to vertical axis, line of collimation perpendicular to horizontal axis.

                2. ANGLE MEASUREMENT TECHNIQUES
                - Repetition Method: Angles measured multiple times around circle to eliminate graduation errors.
                - Reiteration Method: Suitable when several angles are to be measured from a common instrument station.

                3. LATITUDE AND DEPARTURE
                - Latitude (L) = Length * cos(θ) (Northing/Southing)
                - Departure (D) = Length * sin(θ) (Easting/Westing)
                - Closing Error e = sqrt( (ΣL)^2 + (ΣD)^2 )
                - Direction tan(α) = (ΣD) / (ΣL)
                - Correction Rules: Bowditch's Rule and Transit Rule.
            """.trimIndent()
        ),
        Note(
            id = "note_eee_circuits_theorems",
            subjectId = "sub_eee_s3_ct",
            semesterId = "branch_eee_sem_3",
            branchId = "branch_eee",
            title = "Network Theorems: Thevenin, Norton & Superposition Solved Problems",
            description = "Detailed circuit analysis covering Thevenin equivalent circuits (Vth, Rth), Norton equivalent circuits (In, Rn), Maximum Power Transfer theorem with proofs.",
            fileType = "PDF",
            fileSize = "3.6 MB",
            fileName = "EEE_Circuit_Theorems_Handwritten.pdf",
            filePath = "uploads/eee/circuits/theorems.pdf",
            uploadDate = "2026-09-04",
            tags = "Electrical, Circuits, Thevenin, Norton, Superposition, Max Power",
            downloadCount = 127,
            content = """
                ==============================================
                ELECTRIC CIRCUITS: NETWORK THEOREMS
                ==============================================
                1. THEVENIN'S THEOREM
                Any linear bilateral active network can be replaced by an equivalent voltage source Vth in series with resistance Rth across two terminals.
                - Vth = Open circuit voltage across load terminals A-B.
                - Rth = Resistance across A-B with all independent voltage sources shorted and current sources opened.

                2. NORTON'S THEOREM
                Any linear network can be converted into an equivalent current source In in parallel with resistance Rn.
                - In = Short circuit current between terminals A-B.
                - Rn = Rth.
                - Relationship: Vth = In * Rth.

                3. MAXIMUM POWER TRANSFER THEOREM
                Maximum power is transferred from source to load when Load Resistance RL equals Internal Source Resistance Rth:
                - RL = Rth (for DC circuits).
                - Maximum Power P_max = (Vth^2) / (4 * Rth).
            """.trimIndent()
        )
    )
}
