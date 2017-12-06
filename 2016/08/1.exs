defmodule Puzzle do
  defstruct(
    board: [],
    height: 1,
    input: "",
    instructions: [],
    width: 1
  )
end

defmodule Rect do
  defstruct(
    width: 1,
    height: 1
  )
end

defmodule Rotate do
  defstruct(
    axis: :x,
    distance: 0,
    index: 0
  )
end

defmodule Puzzle8_1 do

  @tests [
    {
      %Puzzle{
        width: 6,
        height: 4,
        input: "
          rect 4x3
        "
      },
      "
        ####...
        ####...
        ####...
        .......
      "
    },
    #     {
    #       %Puzzle{
    #         width: 7,
    #         height: 3,
    #         input: "
    #           rect 3x2
    #           rotate column x=1 by 1
    #           rotate row y=0 by 4
    #           rotate column x=1 by 1
    #         "
    #       },
    #       "
    #         .#..#.#
    #         #.#....
    #         .#.....
    #       "
    #     },
    #     {
    #       %Puzzle{
    #         width: 7,
    #         height: 3,
    #         input: "
    #           rect 3x2
    #           rotate column x=1 by 1
    #           rotate row y=0 by 4
    #           rotate column x=1 by 1
    #         "
    #       },
    #       "
    #         .#..#.#
    #         #.#....
    #         .#.....
    #       "
    #     },
  ]

  def solve(%Puzzle{} = puzzle) do
    puzzle
    |> make_board()
    |> extract_instructions()
    |> apply_instructions()
    |> IO.inspect()
    |> stringify_board()
  end

  def make_board(%Puzzle{ width: width, height: height } = puzzle) do
    board = (1..height)
    |> Enum.map(fn (_row) ->
      Enum.map((1..width), fn (_col) -> "." end)
    end)
    Map.put(puzzle, :board, board)
  end

  def stringify_board(%Puzzle{ board: board }) do
    board
    |> Enum.map(&Enum.join(&1, ""))
    |> Enum.join("\n")
  end

  def extract_instructions(%Puzzle{ input: input } = puzzle) do
    instructions = input
                   |> String.split("\n")
                   |> Enum.reject(fn (line) -> String.match?(line, ~r/^\s*$/) end)
                   |> Enum.map(fn (line) ->
                     parts = line
                             |> IO.inspect()
                             |> String.trim()
                             |> String.split(~r/\s+/)
                     apply(__MODULE__, :line_to_instruction, parts)
                   end)

    Map.put(puzzle, :instructions, instructions)
  end

  def line_to_instruction("rect", width_by_height) do
    [width, height] = String.split(width_by_height, "x")
    %Rect{
      width: parse_int(width),
      height: parse_int(height)
    }
  end

  def line_to_instruction("rotate", "row", "y=" <> index, "by", distance) do
    %Rotate{
      axis: :y,
      index: parse_int(index),
      distance: parse_int(distance)
    }
  end

  def line_to_instruction("rotate", "column", "x=" <> index, "by", distance) do
    %Rotate{
      axis: :x,
      index: parse_int(index),
      distance: parse_int(distance)
    }
  end

  def apply_instructions(%Puzzle{ board: board, instructions: instructions } = puzzle) do
    applied_board = Enum.reduce(instructions, board, &next_board(&2, &1))
    Map.put(puzzle, :board, applied_board)
  end

  def next_board(last_board, %Rect{ width: width, height: height }) do
    updates = (1..height)
              |> Enum.map(fn (row) ->
                Enum.map((1..width), fn (col) ->
                  led(width, height, row, col)
                end)
              end)

    Enum.reduce(last_board, fn (board, {getter_or_setter, args}) ->
      apply(getter_or_setter, Enum.flatten([board, args], 1))
    end)
  end

  def led(width, height, x, y) when x < width - 1 and y < height - 1 do
    {:put_in, [[Access.at(y), Access.at(x)], "#"]}
  end

  def led(_width, _height, x, y) do
    {:get_in, [[Access.at(y), Access.at(x)]]}
  end

  def print_result(true), do: IO.puts(" ✅")
  def print_result(false), do: IO.puts(" 💥")

  def run() do
    run_tests
    #    run_puzzle
  end

  def run_puzzle() do
    IO.write("[Puzzle]: ")
    {:ok, input} = File.read(Path.expand("input.txt", __DIR__))
    puzzle = %Puzzle{
      width: 50,
      height: 6,
      input: input
    }

    solve(puzzle)
    |> IO.puts
  end

  def run_tests() do
    @tests
    |> Enum.with_index
    |> Enum.each(fn ({{%Puzzle{} = puzzle, expected}, index}) ->
      test("Test #{index}", puzzle, trim(expected))
    end)
  end

  def trim(expected) do
    expected
    |> String.split(~r/[\r\n]/)
    |> Enum.map(&String.trim(&1))
    |> Enum.reject(&empty?(&1))
    |> Enum.join("\n")
  end

  def empty?(string) do
    String.length(string) == 0
  end

  def test(label, %Puzzle{} = puzzle, expected) do
    actual = solve(puzzle)
    IO.write("[#{label}]: expected '#{expected}', actual '#{actual}'")
    print_result(actual == expected)
    actual
  end

  def parse_int(string), do: elem(Integer.parse(string), 0)
end

Puzzle8_1.run()
