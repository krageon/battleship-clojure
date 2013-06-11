# Battleship
This is an implementation of battleship in noir. 
Battleship (also Battleships or Sea Battle) is a guessing game for two players. It is known worldwide as a pencil and paper game which dates from World War I. 
It was published by various companies as a pad-and-pencil game in the 1930s, and was released as a plastic board game by Milton Bradley in 1967.

## Rules
The game is played on four grids, two for each player. The grids are typically square – usually 10×10 – and the individual squares in the grid are identified by letter and number.
 On one grid the player arranges ships and records the shots by the opponent. On the other grid the player records his/her own shots.
Before play begins, each player secretly arranges their ships on their primary grid.
Each ship occupies a number of consecutive squares on the grid, arranged either horizontally or vertically. 
The number of squares for each ship is determined by the type of the ship. The ships cannot overlap (i.e., only one ship can occupy any given square in the grid). 
The types and numbers of ships allowed are the same for each player. These may vary depending on the rules.

```
[#]	[Ship Name] 		[Size]
[1]	[Aircraft Carrier]	[5]
[1]	[Battleship]		[4]
[1]	[Cruiser]			[3]
[2] [Destroyer]			[2]
[2]	[Submarines]		[1]
```

After the ships have been positioned, the game proceeds in a series of rounds.
In each round, each player takes a turn to announce a target square in the opponent's grid which is to be shot at. 
The opponent announces whether or not the square is occupied by a ship, and if it is a "hit" they mark this on their own primary grid. 
The attacking player notes the hit or miss on their own "tracking" grid, in order to build up a picture of the opponent's fleet.

When all of the squares of a ship have been hit, the ship is sunk, and the ship's owner announces this (e.g. "You sunk my battleship!"). 
If all of a player's ships have been sunk, the game is over and their opponent wins.

# For tutor 
```
model
then view
then eat dumpling'
google 'How to eat dumplings'
```

# General
## Running
If you use cake, substitute 'lein' with 'cake' below. Everything should work fine.

```bash
lein deps
lein run
```

## How To Play
```place ships
'go to battle'
click things to shoot them
try to hit more things than the other guy
```

## License

Distributed under the GNU Affero General Public License, at http://www.gnu.org/licenses/agpl-3.0.html