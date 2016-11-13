| Octet         | Instruction   | Description                                       |
| ------------- |:-------------:| -------------------------------------------------:|
| 2a            | aload_0       | read ref to first argument (this ref in our case) |
| b4 00 02      | getfield      | get field coins from this                         |
| 1b            | iload_1       | load int ratio from argument                      |
| 68            | imul          | multiply ratio and coins                          |
| 3d            | istore_2      | store result into local var 2                     |
| 2a            | aload_0       | load this ref                                     |
| 1c            | iload_2       | load var 2 (result of multiplication)             |
| b5 00 02      | putfield      | save result into field coins                      |
| b1            | return        | return from method                                |
