# Java Communication Layer
## Description
This repository contains resources and source code for the **info5.sar** project. This project aims to create multiple Java Socket designs/implementations based on Threaded or Event-driven models.

## Repository Structure
- **/task1**: Eclipse project containing Threaded Channel/Broker + Threaded Message Overlay
- **/task3**: Eclipse project containing Threaded Channel + Mixed Thread/Event Message Overlay
- **/task4**: Eclipse project containing Full Event Message Layer (Made by MIRAS Romain)
- **/rmiras**: Eclipse project containing Full Event Message Layer (Made by Authors)
- **/design**: Folder containing designs for each task.
- **/specification**: Folder containing specifications for each task.

*Note: Each Eclipse project contains a test package with an EchoServer class having a main method that tests the sockets with a simple send/receive test.*

## Final Project

This project is an Full Event Communication Layer made in Java with two distinct layers:
- A bit-sending layer (Channel/Broker)
- A message sending layer ( QueueBroker/MessageQueue) 
  
The files that follow contain the final project produced by all the authors :

- [Specification](./specification/final%20task4%20specification.md)
- [Design](./design/final%20task4%20design.md)
- [Eclipse Project](./rmiras/src/)
- [Tests](./rmiras/src/tests/)

Here are the different packages that can be found in the project :

- [Bit-Sending Layer Interface](./rmiras/src/ichannels/)
- [Bit-Sending Layer Implementation](./rmiras/src/channels/)
- [Message sending Layer Interface](./rmiras/src/imessages/)
- [Message sending Layer Implementation](./rmiras/src/messages/)
- [Event System Implementation](./rmiras/src/event/)

## Branch Info

The main branch containing the latest version is the **main** branch.
Each task was implemented in order with the creation of a branch for each advancement stage: specification -> design -> implementation.
Tests are generally created in advance in the implementation branch.

## Authors
- [DEL MEDICO Rémi](https://github.com/RDel-Medico)
- [ARLE Alexandre](https://github.com/arlealexandre/)
- [MIRAS Romain](https://github.com/RomainMIRAS/)

Thank you for checking out this repository!