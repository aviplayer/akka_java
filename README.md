Step #1: Simple Folder

At the first step you need to create a simple actor system with two actors:

- Printer

- Folder

Printer is an actor that is responsible for printing every message it receives to log.

Folder - actor that represents simple folder entity with next attributes:

- id - integer value, unique identifier, cannot be changed;

- name - String value;

- createAt - timestamp value, cannot be changed;

- modifiedAt - timestamp value, cannot be changed.

Folder is able to receive the next messages:

- GetData(replyTo) - actor need to send data to `replyTo` actor;

- GetDataConditionally(nameCondition, replyTo) - if folder’s name matches condition actor have to send data to `replyTo` otherwise it must send ConditionNotMet message to `replyTo`

- UpdateName(name) - actor should update name and modifiedAt properties;

- Delete - actor should be gracefully stopped.

Depending on implementation messages can be sent via rootBehavior (akka typed) or from outside of actor system (akka classic).
