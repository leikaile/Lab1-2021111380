import java.io.*;
import java.util.*;

public class Lab1 {
    private static Map<String, Map<String, Integer>> graph = new HashMap<>();

    public static void main(String[] args) {
        if (args.length > 0) {
            String filePath = args[0];
            readFileAndCreateGraph(filePath);
        } else {
            System.out.println("Please provide a file path.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Show directed graph");
            System.out.println("2. Query bridge words");
            System.out.println("3. Generate new text");
            System.out.println("4. Calculate shortest path");
            System.out.println("5. Random walk");
            System.out.println("6. Generate and visualize graph");
            System.out.println("0. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    showDirectedGraph();
                    break;
                case 2:
                    System.out.print("Enter two words: ");
                    String word1 = scanner.next();
                    String word2 = scanner.next();
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 3:
                    System.out.print("Enter a new text: ");
                    String inputText = scanner.nextLine();
                    System.out.println(generateNewText(inputText));
                    break;
                case 4:
                    System.out.print("Enter two words: ");
                    word1 = scanner.next();
                    word2 = scanner.next();
                    System.out.println(calcShortestPath(word1, word2));
                    break;
                case 5:
                    randomWalk();
                    break;
                case 6:
                    System.out.print("Enter output image file name (e.g., graph.png): ");
                    String fileName = scanner.next();
                    generateAndVisualizeGraph(fileName, null);
                    System.out.println("Graph image generated: " + fileName);
                    break;
                case 0:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    //
    private static void readFileAndCreateGraph(String filePath) {
        // 尝试创建一个BufferedReader对象，用于读取文件内容
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line; // 定义一个字符串变量，用于存储每行读取的内容
            String prevWord = null; // 定义一个字符串变量，用于存储前一个单词
            // 循环读取文件中的每一行
            while ((line = reader.readLine()) != null) {
                // 将行内容转换为小写，并将所有非字母和空格字符替换为空格
                line = line.toLowerCase().replaceAll("[^a-z\\s]", " ");
                // 按空格分割行内容，得到单词数组
                String[] words = line.split("\\s+");
                // 遍历单词数组
                for (String word : words) {
                    // 如果单词为空，则跳过
                    if (word.isEmpty()) continue;
                    // 如果图中不包含该单词，则将其添加到图中
                    if (!graph.containsKey(word)) {
                        graph.put(word, new HashMap<>());
                    }
                    // 如果前一个单词不为空，则将当前单词作为前一个单词的一个边添加到图中
                    if (prevWord != null) {
                        Map<String, Integer> edges = graph.get(prevWord);
                        edges.put(word, edges.getOrDefault(word, 0) + 1);
                    }
                    // 更新前一个单词为当前单词
                    prevWord = word;
                }
            }
            // 捕获并处理IO异常
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void showDirectedGraph() {
        //遍历图中的每个节点，graph.keySet() 返回图中所有节点的集合。
        for (String node : graph.keySet()) {
            System.out.print(node + " -> ");
            Map<String, Integer> edges = graph.get(node);//获取当前节点的相邻节点及对应的边权重。
            //遍历当前节点的相邻节点及对应的边权重。
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                System.out.print(edge.getKey() + "(" + edge.getValue() + ") ");//打印相邻节点的名称和对应的边权重。
            }
            System.out.println();
        }
    }

    private static String queryBridgeWords(String word1, String word2) {
        // 检查图中是否包含word1或word2，如果不包含则返回相应的提示信息
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // 创建一个集合用于存储桥接词
        Set<String> bridgeWords = new HashSet<>();

        // 遍历word1的所有邻接点
        for (String intermediate : graph.get(word1).keySet()) {
            // 如果邻接点的邻接点中包含word2，则将该邻接点添加到桥接词集合中
            if (graph.get(intermediate).containsKey(word2)) {
                bridgeWords.add(intermediate);
            }
        }

        // 如果桥接词集合为空，返回没有桥接词的提示信息
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }

        // 否则返回桥接词列表
        return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
    }


    private static String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");//将输入文本转换为小写，并按空格分割为单词数组。
        StringBuilder newText = new StringBuilder();//创建一个 StringBuilder 对象，用于构建生成的新文本
        //循环遍历输入文本中的单词，不包括最后一个单词。
        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");//将当前单词添加到新文本中，并加上空格。
            Set<String> bridgeWords = new HashSet<>();//创建一个 HashSet 用于存储桥接词。
            //检查当前单词是否在图中存在。
            if (graph.containsKey(words[i])) {
                //遍历当前单词的相邻节点。
                for (String intermediate : graph.get(words[i]).keySet()) {
                    //检查相邻节点是否包含下一个单词。
                    if (graph.get(intermediate).containsKey(words[i + 1])) {
                        //将符合条件的相邻节点（桥接词）添加到集合中。
                        bridgeWords.add(intermediate);
                    }
                }
            }
            //检查是否存在桥接词。
            if (!bridgeWords.isEmpty()) {
                //将桥接词集合转换为列表。
                List<String> bridgeWordsList = new ArrayList<>(bridgeWords);
                //从桥接词列表中随机选择一个桥接词
                String bridgeWord = bridgeWordsList.get(new Random().nextInt(bridgeWordsList.size()));
                //将选定的桥接词添加到新文本中，并加上空格。
                newText.append(bridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    private static String calcShortestPath(String word1, String word2) {
        // 如果图中不包含word1或word2，返回提示信息
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // 创建一个哈希映射，用于存储从起点到每个节点的最短距离
        Map<String, Integer> distances = new HashMap<>();
        // 创建一个哈希映射，用于存储每个节点的前驱节点
        Map<String, String> previous = new HashMap<>();
        // 创建一个优先队列，用于存储待处理的节点，按距离排序
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // 初始化所有节点的距离为无穷大
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        // 设置起点的距离为0
        distances.put(word1, 0);
        // 将起点加入优先队列
        pq.add(word1);

        // Dijkstra算法的主要循环
        while (!pq.isEmpty()) {
            // 从优先队列中取出距离最小的节点
            String current = pq.poll();
            // 如果当前节点是目标节点，则提前退出
            if (current.equals(word2)) break;

            // 遍历当前节点的所有邻居节点
            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                // 计算从起点到邻居节点的新距离
                int newDist = distances.get(current) + neighbor.getValue();
                // 如果新距离小于当前记录的距离，更新距离和前驱节点，并将邻居节点加入优先队列
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        // 如果目标节点的距离仍为无穷大，说明不可达，返回提示信息
        if (distances.get(word2) == Integer.MAX_VALUE) {
            return word2 + " is not reachable from " + word1;
        }

        // 通过前驱节点映射构建从起点到目标节点的路径
        List<String> path = new ArrayList<>();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.add(at);
        }
        // 反转路径，使其从起点到目标节点
        Collections.reverse(path);

        // 生成并可视化包含路径的图像
        String outputFileName = "graph_with_path.png";
        generateAndVisualizeGraph(outputFileName, path);

        // 返回最短路径及其长度，并提示生成的图像文件
        return "Shortest path: " + String.join(" -> ", path) + " (length: " + distances.get(word2) + ")\nGraph image generated: " + outputFileName;
    }


    private static void randomWalk() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            List<String> nodes = new ArrayList<>(graph.keySet()); // 获取图中所有节点
            String current = nodes.get(new Random().nextInt(nodes.size())); // 随机选择起始节点
            writer.print(current); // 输出起始节点

            Set<String> visitedEdges = new HashSet<>(); // 用于记录访问过的边

            while (true) { // 开始随机游走循环
                Map<String, Integer> neighbors = graph.get(current); // 获取当前节点的相邻节点
                if (neighbors.isEmpty()) break; // 如果当前节点没有相邻节点，则结束游走

                List<String> edges = new ArrayList<>(neighbors.keySet()); // 将相邻节点转换为列表
                String next = edges.get(new Random().nextInt(edges.size())); // 随机选择下一个相邻节点
                String edge = current + "->" + next; // 构建当前节点到下一个节点的边

                if (visitedEdges.contains(edge)) break; // 如果已经访问过这条边，则结束游走
                visitedEdges.add(edge); // 将当前边标记为已访问

                writer.print(" " + next); // 输出下一个节点
                current = next; // 更新当前节点为下一个节点
            }
        } catch (IOException e) {
            e.printStackTrace(); // 捕获并打印IO异常
        }
    }


    private static void generateDotFile(String fileName, List<String> path) {
        // 方法开始：生成指定文件名的.dot文件，用于可视化图形表示
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            // 写入dot文件头部，表示开始一个有向图
            writer.println("digraph G {");
            // 遍历图的节点
            for (String node : graph.keySet()) {
                // 获取当前节点的边集合
                Map<String, Integer> edges = graph.get(node);
                // 遍历当前节点的边
                for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                    // 如果指定了路径，并且当前节点和下一个节点都在路径中，并且下一个节点是当前节点的后继
                    if (path != null && path.contains(node) && path.contains(edge.getKey()) && path.indexOf(edge.getKey()) == path.indexOf(node) + 1) {
                        // 写入当前节点到下一个节点的边，标记为红色，加粗，并标注权重
                        writer.printf("    \"%s\" -> \"%s\" [label=\"%d\", color=\"red\", penwidth=2.0];\n", node, edge.getKey(), edge.getValue());
                    } else {
                        // 写入当前节点到下一个节点的边，并标注权重
                        writer.printf("    \"%s\" -> \"%s\" [label=\"%d\"];\n", node, edge.getKey(), edge.getValue());
                    }
                }
            }
            // 写入dot文件尾部
            writer.println("}");
        } catch (IOException e) {
            // 捕获IO异常并打印异常信息
            e.printStackTrace();
        }
    }


    private static void generateAndVisualizeGraph(String outputFileName, List<String> path) {
        // 定义生成dot文件的文件名
        String dotFileName = "graph.dot";
        // 调用生成dot文件的方法，传入dot文件名和路径
        generateDotFile(dotFileName, path);

        try {
            // 创建一个ProcessBuilder对象，用于运行外部程序'dot'，生成PNG图像
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFileName, "-o", outputFileName);
            // 设置错误流与标准输出流合并
            pb.redirectErrorStream(true);
            // 启动进程
            Process process = pb.start();
            // 尝试读取进程的输出流
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                // 逐行读取并打印输出流内容
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            // 等待进程完成
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            // 捕获并打印IO异常或中断异常的堆栈跟踪信息
            e.printStackTrace();
        }
    }

}
