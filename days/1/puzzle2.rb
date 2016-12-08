require 'bigdecimal'
require 'set'

puzzle_input = <<~INPUT
L4,L1,R4,R1,R1,L3,R5,L5,L2,L3,R2,R1,L4,R5,R4,L2,R1,R3,L5,R1,L3,L2,R5,L4,L5,R1,R2,L1,R5,L3,R2,R2,L1,R5,
R2,L1,L1,R2,L1,R1,L2,L2,R4,R3,R2,L3,L188,L3,R2,R54,R1,R1,L2,L4,L3,L2,R3,L1,L1,R3,R5,L1,R5,L1,L1,R2,R4,
R4,L5,L4,L1,R2,R4,R5,L2,L3,R5,L5,R1,R5,L2,R4,L2,L1,R4,R3,R4,L4,R3,L4,R78,R2,L3,R188,R2,R3,L2,R2,R3,R1,
R5,R1,L1,L1,R4,R2,R1,R5,L1,R4,L4,R2,R5,L2,L5,R4,L3,L2,R1,R1,L5,L4,R1,L5,L1,L5,L1,L4,L3,L5,R4,R5,R2,L5,
R5,R5,R4,R2,L1,L2,R3,R5,R5,R5,L2,L1,R4,R3,R1,L4,L2,L3,R2,L3,L5,L2,L2,L1,L2,R5,L2,L2,L3,L1,R1,L4,R2,L4,
R3,R5,R3,R4,R1,R5,L3,L5,L5,L3,L2,L1,R3,L4,R3,R2,L1,R3,R1,L2,R4,L3,L3,L3,L1,L2
INPUT

test1_input = <<~INPUT
R8, R4, R4, R8
INPUT

def make_direction(direction)
  case direction
  when 'L' then method(:turn_left)
  when 'R' then method(:turn_right)
  else raise "Unknown direction #{direction}"
  end
end

def make_directional_pair(direction)
  [make_direction(direction[0]), direction[1..-1].to_i]
end

def turn_left(accumulator)
  direction = case accumulator[:direction]
              when :north then :west
              when :south then :east
              when :east then :north
              when :west then :south
              end
  accumulator.merge({ direction: direction })
end

def turn_right(accumulator)
  direction = case accumulator[:direction]
              when :north then :east
              when :south then :west
              when :east then :south
              when :west then :north
              end
  accumulator.merge({ direction: direction })
end

def visited?(accumulator)
  coordinates = { x: accumulator[:x], y: accumulator[:y] }
  has_visited = accumulator[:visited].include?(coordinates)
  if has_visited
    accumulator[:found] = true
  else
    accumulator[:visited].add(coordinates)
  end
  has_visited
end

def add(accumulator, plane, distance)
  (1..distance).take_while do |n|
    accumulator[plane] += 1
    !visited?(accumulator)
  end
  accumulator
end

def sub(accumulator, plane, distance)
  (1..distance).take_while do |n|
    accumulator[plane] -= 1
    !visited?(accumulator)
  end
  accumulator
end

def walk(accumulator, distance)
  case accumulator[:direction]
  when :north then add(accumulator, :x, distance)
  when :south then sub(accumulator, :x, distance)
  when :east then add(accumulator, :y, distance)
  when :west then sub(accumulator, :y, distance)
  end
end

def tick(accumulator, pair)
  turn = pair[0]
  distance = pair[1]
  walk(turn.call(accumulator), distance)
end

def solve(input)
  directions = input
    .gsub(/\s+/, '')
    .split(',')
    .map(&method(:make_directional_pair))

  accumulator = { direction: :north, x: 0, y: 0, visited: Set.new, found: false }
  directions.take_while do |direction|
    accumulator = tick(accumulator, direction)
    !accumulator[:found]
  end
  abs(accumulator[:x]) + abs(accumulator[:y])
end

def abs(n)
  BigDecimal.new(n).abs.to_i
end

puts "Test 1: #{solve(test1_input)} should == 4"
puts "Puzzle: #{solve(puzzle_input)}"
