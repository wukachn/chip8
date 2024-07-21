# Chip8

CHIP-8 is a simple, interpreted programming language that was developed in the 1970s, originally designed for the COSMAC VIP and Telmac 1800 microcomputers. Over the years, CHIP-8 has remained a popular platform for running classic games and applications due to its straightforward architecture and ease of implementation. This project aims to provide a fully functional CHIP-8 emulator and interpreter, allowing you to experience and explore these vintage programs on modern hardware.

<img width="500" alt="Screenshot 2024-07-21 at 18 10 13" src="https://github.com/user-attachments/assets/ebc441e7-d685-4017-88e7-8a78ccf91e34">

## How to run

1. `mvn clean compile assembly:single`
2. `java -jar target/chip8-1.0-SNAPSHOT-jar-with-dependencies.jar <ROM-PATH>`

With there being various CHIP-8 implementations out there, there is not a single true specification. This means that not all ROMs can be expected to run without further __qiurk__ configuartion. A handful of publically available ROMs can be found in the `roms` directory, so that you can play around with the project.

## Controls

The following keys are used to control the program, each rom will differ in controls.

| | | | | 
| - | - | - | - |
| 1 | 2 | 3 | 4 | 
| q | w | e | r | 
| a | s | d | f | 
| z | x | c | v | 

## Resources

 - https://tobiasvl.github.io/blog/write-a-chip-8-emulator/
 - https://github.com/Timendus/chip8-test-suite
