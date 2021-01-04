##Step #1: Simple Folder

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


##Step #2: Folder Collection

On this step FolderCollection actor should be created. This actor should store all folders and generate id for them.

FolderCollection should be able to receive the next messages:

- CreateFolder(name) - new Folder actor should be created with given name and next integer id
- UpdateFolderById(id, name) - updates Folder with given id by setting given name (modifiedAt field should be updated as well);
- DeleteFolderById(id) - deletes folder with given id;
- GetFolderById(id, replyTo) - sends folder data to `replyTo` actor;
- GetAllFolders(replyTo) - sends array of all folders data to `replyTo` actor (may be presented as array of tuples);
- GetFoldersConditionally(nameCondition, replyTo) - sends array of folder data representing folders that met condition.

###Step #3: Persistence
On this step akka persistence capabilities should be applied to folder collection and folders
themselve. Actors should process the same messages but do it in the persistent way so after
restart the state of folder collection and folders should remain.

###Step #4: Clustering
On this step akka cluster should be added. FolderCollection should become a singleton actor,
so it will be accessible from any akka node. All messages should stay the same.

###Step #5: Spring Integration
