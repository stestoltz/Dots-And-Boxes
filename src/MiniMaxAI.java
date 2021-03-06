
public class MiniMaxAI extends Player {

	private int ply;

	public MiniMaxAI(int color, int ply) {
		super(color);

		this.ply = ply;
	}

	// timing how long we are in this algorithm
	public static long totalTimeNanos = 0;

	@Override
	public Move play(Board b) {

		// copy the board
		Board board = new Board(b);

		// the best move
		MoveValue max;

		long startTime = System.nanoTime();

		try {
			// run mini-max
			max = maxValue(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		} catch (Exception e) {
			max = new MoveValue(0);
			System.out.println(e.getMessage());
		}

		long endTime = System.nanoTime();

		// increase the timer
		totalTimeNanos += (endTime - startTime);

		// return the best move
		return max.move;
	}

	/**
	 * max player
	 * @param board
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 * @throws Exception
	 */
	public MoveValue maxValue(Board board, int alpha, int beta, int depth) throws Exception {
		// if we have reached our depth or the game is over, return the current utility
		if (depth >= ply || board.isGameOver()) {
			return new MoveValue(utility(board));
		}

		MoveValue moveValue = new MoveValue(Integer.MIN_VALUE);

		for (Move m : board.getOrderedMoves()) {

			boolean extraTurn = board.placeLine(color, m.first(), m.second());

			// see if continuing branch would increase value
			MoveValue branchValue;
			if (extraTurn) {
				branchValue = maxValue(board, alpha, beta, depth + 1);
			} else {
				branchValue = minValue(board, alpha, beta, depth + 1);
			}

			// undo the previous move in the board object
			board.undoMove(m.first(), m.second());

			// if this move is better, replace the saved one
			if (branchValue.value > moveValue.value) {
				moveValue.value = branchValue.value;
				moveValue.move = m;
			} else {
				
				// if there is no best move, replace the saved one
				if (moveValue.move == null) {
					moveValue.move = m;
				}
			}

			// prune
			if (moveValue.value >= beta) {
				return moveValue;
			}

			// update alpha
			alpha = Math.max(alpha, moveValue.value);
		}

		return moveValue;
	}

	public MoveValue minValue(Board board, int alpha, int beta, int depth) throws Exception {
		// if we have reached our depth or the game is over, return the current utility
		if (depth >= ply || board.isGameOver()) {
			return new MoveValue(utility(board));
		}

		MoveValue moveValue = new MoveValue(Integer.MAX_VALUE);

		for (Move m : board.getOrderedMoves()) {

			boolean extraTurn = board.placeLine(otherColor(), m.first(), m.second());

			// see if continuing branch would decrease value
			MoveValue branchValue;
			if (extraTurn) {
				branchValue = minValue(board, alpha, beta, depth + 1);
			} else {
				branchValue = maxValue(board, alpha, beta, depth + 1);
			}

			// undo the previous move in the board object
			board.undoMove(m.first(), m.second());

			// if this move is better, replace the saved one
			if (branchValue.value < moveValue.value) {
				moveValue.value = branchValue.value;
				moveValue.move = m;
			} else {
				
				// if there is no best move, replace the saved one
				if (moveValue.move == null) {
					moveValue.move = m;
				}
			}

			// prune
			if (moveValue.value <= alpha) {
				return moveValue;
			}

			// update beta
			beta = Math.min(beta, moveValue.value);
		}

		return moveValue;
	}

	// heuristic: our score - their score (max wants to increase, min wants to decrease)
	public int utility(Board b) {
		return b.calculateScore(this.color) - b.calculateScore(otherColor());
	}

}
