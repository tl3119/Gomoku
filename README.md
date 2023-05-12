“Gomoku, also called Five in a Row, is an abstract strategy board game. It is traditionally played with Go pieces (black and white stones) on a Go board. It is played using a 15×15 board while in the past a 19×19 board was standard. Because pieces are typically not moved or removed from the board, gomoku may also be played as a paper-and-pencil game. The game is known in several countries under different names. Players alternate turns placing a stone of their color on an empty intersection. Black plays first. The winner is the first player to form an unbroken line of five stones of their color horizontally, vertically, or diagonally. If the board is completely filled and no one can make a line of 5 stones, then it will result in a draw.”

In our game, there are two file, one is Server, another is Game.java, which represents client side.

To play the game, firstly need to run the server file, and then run the Game file two times.

Game rule:
Player 1 (black stone) can go first, and player 2 (white stone) needs to wait for the player 1 to make a move. After that, two players alternate turns place a stone in the board. If it's not your turn. you can not put your stone on the board.
The winner is the first player to form an unbroken line of five stones of their color horizontally, vertically, or diagonally. If winner appears, game end!  If the board is completely filled and no one can make a line of 5 stones, then it will result in a draw.”

Related Topics, as promised in the Project Proposal:

1. Thread concurrency:

Use multiple threads to manage game logic, user connections, and communication between players.

Implement thread to avoid race conditions or inconsistencies..

2. Networking (sockets):

Use Java sockets to realize a client-server architecture.

The server will manage the game state, update board movements to clients, and receive players' moves.

Clients (players) will send their moves to the server.

3. Graphics:

Use Java's libraries to create a graphical user interface (GUI) for the game, including the game board, player information, and game status display(who is going to move).

The forth topic, which is JDBC, is not implemented after discuss between team members, since the inclusion of JDBC will cause several bugs, and it may cause potential difficulty for grading. We already included three related topics in the project, which follow the project requirement.